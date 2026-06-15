package com.filmlog.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraRoll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.filmlog.ui.theme.StatusLoaded
import com.filmlog.ui.theme.WarmAmber
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordDetailScreen(
    recordId: Long,
    onBack: () -> Unit,
    viewModel: RecordDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd  HH:mm", Locale.getDefault()) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "FRAME",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 3.sp),
                        color = WarmAmber
                    )
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
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = WarmAmber, strokeWidth = 2.dp)
            }
        } else {
            val record = uiState.record ?: return@Scaffold

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // 大号 Frame 编号
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(WarmAmber.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = WarmAmber,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "#${record.frameNumber}",
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Light),
                        color = WarmAmber
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    uiState.film?.let { film ->
                        Text(
                            "${film.brand}  ·  ${film.name}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // 曝光参数 (三个大数字)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ExposureBig("快门", record.shutterSpeed)
                        VerticalDivider(modifier = Modifier.height(40.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        ExposureBig("光圈", record.aperture)
                        VerticalDivider(modifier = Modifier.height(40.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        ExposureBig("ISO", record.effectiveIso.toString())
                    }
                }

                // 其他信息
                val hasExtra = record.focalLength != null || record.focusDistance != null ||
                    !record.filter.isNullOrBlank() || record.isoOffset != 0 || record.exposureCompensation != 0f

                if (hasExtra) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            if (record.isoOffset != 0) {
                                InfoRow("ISO 偏移", if (record.isoOffset > 0) "+${record.isoOffset}" else record.isoOffset.toString())
                            }
                            if (record.exposureCompensation != 0f) {
                                InfoRow(
                                    "曝光补偿",
                                    "${if (record.exposureCompensation > 0) "+" else ""}${String.format("%.1f", record.exposureCompensation)} EV"
                                )
                            }
                            record.focalLength?.let { InfoRow("焦距", "${it}mm") }
                            record.focusDistance?.let { InfoRow("对焦距离", it) }
                            record.filter?.let { InfoRow("滤镜", it) }
                        }
                    }
                }

                // 备注
                if (!record.note.isNullOrBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                "NOTE",
                                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                record.note,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    "记录于  ${dateFormat.format(Date(record.createdAt))}",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun ExposureBig(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Light),
            color = WarmAmber
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
