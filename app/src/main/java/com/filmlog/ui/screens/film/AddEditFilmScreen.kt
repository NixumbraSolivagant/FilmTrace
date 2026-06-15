package com.filmlog.ui.screens.film

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.filmlog.domain.model.FilmFormat
import com.filmlog.domain.model.FilmStatus
import com.filmlog.ui.components.DropdownSelector
import com.filmlog.ui.theme.WarmAmber
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditFilmScreen(
    filmId: Long?,
    onBack: () -> Unit,
    viewModel: AddEditFilmViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) onBack()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            if (uiState.isEditing) "EDIT" else "NEW",
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 3.sp),
                            color = WarmAmber
                        )
                        Text(
                            if (uiState.isEditing) "编辑胶卷" else "添加胶卷",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Light)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 常用胶卷预设（横向滚动 chip 行）
            if (!uiState.isEditing) {
                SectionLabel("常用预设")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    viewModel.presets.forEach { preset ->
                        val isSelected = uiState.brand == preset.brand &&
                                uiState.name == preset.name &&
                                uiState.iso == preset.iso.toString()
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.applyPreset(preset) },
                            label = {
                                Text("${preset.brand} ${preset.name}", style = MaterialTheme.typography.bodySmall)
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = WarmAmber.copy(alpha = 0.2f),
                                selectedLabelColor = WarmAmber
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                selectedBorderColor = WarmAmber.copy(alpha = 0.6f)
                            )
                        )
                    }
                }
            }

            SectionLabel("基本")
            OutlinedTextField(
                value = uiState.brand,
                onValueChange = viewModel::updateBrand,
                label = { Text("品牌") },
                placeholder = { Text("如：Kodak, Ilford, Fujifilm") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = WarmAmber,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::updateName,
                label = { Text("型号") },
                placeholder = { Text("如：Portra 400, HP5 Plus") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = WarmAmber,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            OutlinedTextField(
                value = uiState.iso,
                onValueChange = viewModel::updateIso,
                label = { Text("ISO") },
                placeholder = { Text("如：400") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = WarmAmber,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Spacer(modifier = Modifier.height(4.dp))
            SectionLabel("规格")
            DropdownSelector(
                items = FilmFormat.entries.map { it.displayName },
                selectedItem = uiState.format.displayName,
                onItemSelected = { name ->
                    FilmFormat.entries.find { it.displayName == name }?.let { viewModel.updateFormat(it) }
                },
                label = "画幅"
            )

            Spacer(modifier = Modifier.height(4.dp))
            SectionLabel("总张数")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CommonShotCounts.forEach { count ->
                    val isSelected = !uiState.isCustomShots && uiState.totalShots == count.toString()
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectCommonShots(count) },
                        label = { Text("${count}张") },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = WarmAmber.copy(alpha = 0.2f),
                            selectedLabelColor = WarmAmber
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            selectedBorderColor = WarmAmber.copy(alpha = 0.6f)
                        )
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = uiState.isCustomShots,
                    onClick = { viewModel.enableCustomShots() },
                    label = { Text("自定义") },
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = WarmAmber.copy(alpha = 0.2f),
                        selectedLabelColor = WarmAmber
                    )
                )
                if (uiState.isCustomShots) {
                    OutlinedTextField(
                        value = uiState.totalShots,
                        onValueChange = viewModel::updateTotalShots,
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text("张数") },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WarmAmber,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                    Text("张", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            SectionLabel("状态")
            DropdownSelector(
                items = FilmStatus.entries.map { it.displayName },
                selectedItem = uiState.status.displayName,
                onItemSelected = { name ->
                    FilmStatus.entries.find { it.displayName == name }?.let { viewModel.updateStatus(it) }
                },
                label = "当前状态"
            )

            // 库存卷数（仅在新增 + 状态=STOCK 时显示）
            if (!uiState.isEditing && uiState.status == FilmStatus.STOCK) {
                Spacer(modifier = Modifier.height(4.dp))
                SectionLabel("库存卷数")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedIconButton(
                        onClick = viewModel::decrementQuantity,
                        enabled = uiState.quantity > 1,
                        modifier = Modifier.size(44.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "减少")
                    }
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "${uiState.quantity} 卷",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Light),
                            color = WarmAmber
                        )
                    }
                    OutlinedIconButton(
                        onClick = viewModel::incrementQuantity,
                        enabled = uiState.quantity < 99,
                        modifier = Modifier.size(44.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "增加")
                    }
                }
                Text(
                    "保存后将一次性入库 ${uiState.quantity} 卷相同的胶卷。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = viewModel::save,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isSaving,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = WarmAmber,
                    contentColor = Color.Black
                )
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.Black,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text(
                        if (uiState.isEditing) "保存更改" else "添加胶卷",
                        style = MaterialTheme.typography.titleMedium.copy(letterSpacing = 0.5.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp, fontWeight = FontWeight.Medium),
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
