package com.filmlog.ui.screens.shoot

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.filmlog.domain.model.ExposureValues
import com.filmlog.domain.model.Film
import com.filmlog.domain.model.FilmStatus
import com.filmlog.ui.components.DropdownSelector
import com.filmlog.ui.components.WheelPicker
import com.filmlog.ui.theme.DarkBackground
import com.filmlog.ui.theme.WarmAmber
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ShootScreen(
    onNavigateToAddFilm: () -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: ShootViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAdvanced by remember { mutableStateOf(false) }
    var showFinishedDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.saveResult.collectLatest { result ->
            when (result) {
                is SaveResult.FilmFinished -> showFinishedDialog = true
                is SaveResult.Success -> snackbarHostState.showSnackbar("已保存拍摄记录")
                is SaveResult.NoFilmLoaded -> snackbarHostState.showSnackbar("请先装载一卷胶卷")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "SHOOT",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 3.sp),
                    color = WarmAmber
                )
                Text(
                    "拍摄",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Light),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

            if (uiState.loadedFilm == null) {
                NoFilmLoadedCard(onNavigateToAddFilm = onNavigateToAddFilm)
            } else {
                LoadedFilmInfoCard(
                    uiState = uiState,
                    onSwitchFilmClick = viewModel::showFilmPicker
                )

                Spacer(modifier = Modifier.height(16.dp))

                ExposureCard(
                    uiState = uiState,
                    onShutterSpeedChange = viewModel::updateShutterSpeed,
                    onApertureChange = viewModel::updateAperture,
                    onIsoOffsetChange = viewModel::updateIsoOffset,
                    showAdvanced = showAdvanced,
                    onToggleAdvanced = { showAdvanced = !showAdvanced },
                    onFocalLengthChange = viewModel::updateFocalLength,
                    onFocusDistanceChange = viewModel::updateFocusDistance,
                    onExposureCompChange = viewModel::updateExposureCompensation,
                    onFilterChange = viewModel::updateFilter,
                    onNoteChange = viewModel::updateNote
                )

                Spacer(modifier = Modifier.height(16.dp))

                val film = uiState.loadedFilm
                val reachedLimit = film != null && uiState.savedRecordsCount >= film.totalShots

                Button(
                    onClick = viewModel::saveRecord,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    enabled = !uiState.isSaving && film?.status == FilmStatus.LOADED,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WarmAmber,
                        contentColor = DarkBackground,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            if (reachedLimit) "已拍满 · 换新胶卷" else "保存拍摄",
                            style = MaterialTheme.typography.titleMedium.copy(letterSpacing = 0.5.sp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onNavigateToHistory,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("查看历史记录 (${uiState.savedRecordsCount} 张)")
                }
            }
        }
    }

    if (uiState.showFilmPicker) {
        FilmPickerDialog(
            availableFilms = uiState.availableFilms,
            currentFilmId = uiState.loadedFilm?.id,
            onFilmSelected = { viewModel.switchFilm(it) },
            onAddNew = onNavigateToAddFilm,
            onUnload = viewModel::unloadCurrentFilm,
            onDismiss = viewModel::hideFilmPicker
        )
    }

    if (showFinishedDialog && uiState.loadedFilm != null) {
        FilmFinishedDialog(
            film = uiState.loadedFilm!!,
            availableFilms = uiState.availableFilms.filter { it.id != uiState.loadedFilm?.id && it.status != FilmStatus.FINISHED },
            onLoadNew = { newFilm ->
                viewModel.acknowledgeFilmFinishedAndLoadNew(uiState.loadedFilm!!)
                viewModel.switchFilm(newFilm)
                showFinishedDialog = false
            },
            onAddNew = {
                viewModel.acknowledgeFilmFinishedAndLoadNew(uiState.loadedFilm!!)
                showFinishedDialog = false
                onNavigateToAddFilm()
            },
            onDismiss = { showFinishedDialog = false }
        )
    }
}

@Composable
private fun NoFilmLoadedCard(onNavigateToAddFilm: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.CameraRoll,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("当前没有装载胶卷", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "先添加并装载一卷胶卷才能开始记录",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onNavigateToAddFilm,
                colors = ButtonDefaults.buttonColors(containerColor = WarmAmber)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("添加胶卷")
            }
        }
    }
}

@Composable
private fun LoadedFilmInfoCard(
    uiState: ShootUiState,
    onSwitchFilmClick: () -> Unit
) {
    val film = uiState.loadedFilm ?: return
    val progress = if (film.totalShots > 0) uiState.savedRecordsCount.toFloat() / film.totalShots else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        film.brand.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        film.name,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "ISO ${film.iso} · ${film.format.displayName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "${uiState.savedRecordsCount}",
                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Light),
                        color = WarmAmber
                    )
                    Text(
                        "of ${film.totalShots} frames",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = WarmAmber,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onSwitchFilmClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = WarmAmber
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, WarmAmber.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("切换 / 装入胶卷", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun FilmPickerDialog(
    availableFilms: List<Film>,
    currentFilmId: Long?,
    onFilmSelected: (Film) -> Unit,
    onAddNew: () -> Unit,
    onUnload: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        icon = {
            Icon(Icons.Default.CameraRoll, contentDescription = null, tint = WarmAmber)
        },
        title = {
            Text(
                "选择胶卷",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Light)
            )
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                if (availableFilms.isEmpty()) {
                    Text(
                        "还没有胶卷，请先添加。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    availableFilms.forEach { film ->
                        val isCurrent = film.id == currentFilmId
                        val statusLabel = when (film.status) {
                            FilmStatus.LOADED -> "已装载"
                            FilmStatus.STOCK -> "库存"
                            FilmStatus.FINISHED -> "已拍完"
                        }
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onFilmSelected(film) },
                            color = if (isCurrent) WarmAmber.copy(alpha = 0.12f) else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    if (isCurrent) Icons.Default.CheckCircle else Icons.Default.CameraRoll,
                                    contentDescription = null,
                                    tint = if (isCurrent) WarmAmber else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "${film.brand} ${film.name}",
                                        style = MaterialTheme.typography.titleSmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        "ISO ${film.iso} · ${film.format.displayName} · $statusLabel · ${film.remainingShots}/${film.totalShots}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onAddNew) { Text("新建胶卷", color = WarmAmber) }
        },
        dismissButton = {
            Row {
                if (currentFilmId != null) {
                    TextButton(onClick = onUnload) { Text("卸下当前") }
                }
                TextButton(onClick = onDismiss) { Text("关闭") }
            }
        }
    )
}

@Composable
private fun FilmFinishedDialog(
    film: Film,
    availableFilms: List<Film>,
    onLoadNew: (Film) -> Unit,
    onAddNew: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(WarmAmber.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = WarmAmber,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        title = {
            Text(
                "这卷胶卷拍满啦",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Light),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column {
                Text(
                    "${film.brand} ${film.name} · ${film.totalShots} 张全部记录完毕。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "选择新一卷：",
                    style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (availableFilms.isEmpty()) {
                    Text(
                        "没有可用的库存胶卷，请新建一卷。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    availableFilms.forEach { newFilm ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onLoadNew(newFilm) },
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CameraRoll, contentDescription = null, tint = WarmAmber, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "${newFilm.brand} ${newFilm.name}",
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        "ISO ${newFilm.iso} · ${newFilm.format.displayName} · ${newFilm.totalShots}张",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onAddNew) { Text("新建胶卷", color = WarmAmber) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}

@Composable
private fun ExposureCard(
    uiState: ShootUiState,
    onShutterSpeedChange: (String) -> Unit,
    onApertureChange: (String) -> Unit,
    onIsoOffsetChange: (Int) -> Unit,
    showAdvanced: Boolean,
    onToggleAdvanced: () -> Unit,
    onFocalLengthChange: (Int?) -> Unit,
    onFocusDistanceChange: (String?) -> Unit,
    onExposureCompChange: (Float) -> Unit,
    onFilterChange: (String) -> Unit,
    onNoteChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
        WheelStepper(
            items = ExposureValues.SHUTTER_SPEEDS,
            selectedIndex = ExposureValues.SHUTTER_SPEEDS.indexOf(uiState.shutterSpeed).let { if (it < 0) 7 else it },
            onSelectedIndexChange = { onShutterSpeedChange(ExposureValues.SHUTTER_SPEEDS[it]) },
            label = "快门",
            modifier = Modifier.weight(1f)
        )

        WheelStepper(
            items = ExposureValues.APERTURES,
            selectedIndex = ExposureValues.APERTURES.indexOf(uiState.aperture).let { if (it < 0) 6 else it },
            onSelectedIndexChange = { onApertureChange(ExposureValues.APERTURES[it]) },
            label = "光圈",
            modifier = Modifier.weight(1f)
        )
        }

        Spacer(modifier = Modifier.height(12.dp))

        WheelStepper(
            items = ExposureValues.ISO_OFFSETS.map { offset ->
                if (offset == 0) "ISO ${uiState.loadedFilm?.iso ?: 400}"
                else {
                    val effectiveIso = (uiState.loadedFilm?.iso ?: 400) + ((uiState.loadedFilm?.iso ?: 400) * offset / 3)
                    "ISO $effectiveIso (${if (offset > 0) "+" else ""}$offset)"
                }
            },
            selectedIndex = ExposureValues.ISO_OFFSETS.indexOf(uiState.isoOffset).coerceAtLeast(0),
            onSelectedIndexChange = { onIsoOffsetChange(ExposureValues.ISO_OFFSETS[it]) },
            label = "ISO 偏移"
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onToggleAdvanced) {
            Icon(
                if (showAdvanced) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null
            )
            Text(if (showAdvanced) "收起高级选项" else "展开高级选项")
        }

        AnimatedVisibility(visible = showAdvanced) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Spacer(modifier = Modifier.height(8.dp))

                DropdownSelector(
                    items = ExposureValues.FOCAL_LENGTHS.map { "${it}mm" },
                    selectedItem = uiState.focalLength?.let { "${it}mm" } ?: "未选择",
                    onItemSelected = { item ->
                        onFocalLengthChange(item.removeSuffix("mm").toIntOrNull())
                    },
                    label = "焦距"
                )

                DropdownSelector(
                    items = ExposureValues.FOCUS_DISTANCES,
                    selectedItem = uiState.focusDistance ?: "未选择",
                    onItemSelected = { onFocusDistanceChange(it) },
                    label = "对焦距离"
                )

                Text(
                    "曝光补偿: ${if (uiState.exposureCompensation >= 0) "+" else ""}${String.format("%.1f", uiState.exposureCompensation)} EV",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Slider(
                    value = uiState.exposureCompensation,
                    onValueChange = onExposureCompChange,
                    valueRange = -3f..3f,
                    steps = 17
                )

                DropdownSelector(
                    items = ExposureValues.FILTERS,
                    selectedItem = uiState.filter,
                    onItemSelected = onFilterChange,
                    label = "滤镜"
                )

                OutlinedTextField(
                    value = uiState.note,
                    onValueChange = onNoteChange,
                    label = { Text("备注") },
                    placeholder = { Text("如：逆光人像") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }
    }
}

/**
 * WheelStepper: 滚轮 + 左右两侧 ▲/▼ 步进按钮
 *
 * - 整体 Row：左 Column(▲上) | 中 WheelPicker(weight=1f) | 右 Column(▼下)
 * - 两个箭头上下叠放（紧凑），IconButton 直径 24dp
 */
@Composable
private fun WheelStepper(
    items: List<String>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    label: String?,
    modifier: Modifier = Modifier
) {
    val safeSelected = selectedIndex.coerceIn(0, items.size - 1)
    val canPrev = safeSelected > 0
    val canNext = safeSelected < items.size - 1

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        WheelPicker(
            items = items,
            selectedIndex = safeSelected,
            onSelectedIndexChange = onSelectedIndexChange,
            label = label,
            modifier = Modifier.weight(1f)
        )
        // 右：上下叠放的 ▲/▼
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = { if (canPrev) onSelectedIndexChange(safeSelected - 1) },
                enabled = canPrev,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "上一档",
                    tint = if (canPrev) WarmAmber
                           else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f),
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(
                onClick = { if (canNext) onSelectedIndexChange(safeSelected + 1) },
                enabled = canNext,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "下一档",
                    tint = if (canNext) WarmAmber
                           else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
