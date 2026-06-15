package com.filmlog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.filmlog.ui.theme.WarmAmber
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * WheelPicker: iOS 风格的统一滚轮
 *
 * - 整块 = 一个 Surface，内部可垂直拖动
 * - 高亮条固定在 Surface 中心 (Alignment.Center)
 * - 当前项永远在高亮条上，上下小号显示相邻项
 * - 拖动 / 点击 / 上方箭头 / 下方箭头（左右其实指上下，命名保留）都能切值
 *
 * 高度 = visibleItemCount * itemHeight
 * 内容高 = items.size * itemHeight
 * 滚动用 verticalScroll + Dp.toPx()（在 graphicsLayer / 渲染阶段用 px）
 * 对齐：scrollPx = selectedIndex * itemHeightPx - centerIndexOffset * itemHeightPx
 *       此时第 selectedIndex 项顶部 = 中心 + 0，中心 = centerIndexOffset * itemHeight
 *       第 selectedIndex 项中心 = (centerIndexOffset + 0.5) * itemHeight = 可见区中心
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WheelPicker(
    items: List<String>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null
) {
    val safeSelected = selectedIndex.coerceIn(0, items.size - 1)

    val itemHeight: Dp = 36.dp
    val visibleItemCount = 3
    val centerIndexOffset = visibleItemCount / 2  // = 1

    val density = LocalDensity.current
    val itemHeightPx = with(density) { itemHeight.toPx() }
    val listHeight: Dp = itemHeight * visibleItemCount
    val spacerHeightPx = centerIndexOffset * itemHeightPx

    val scrollState = rememberScrollState()
    var didInitialScroll by remember { mutableStateOf(false) }

    // 初始：让第 safeSelected 项中心对齐到高亮条
    // 推导：viewport 5H, 高亮条中心 2.5H, item N 顶部 = 2H + N*H - scrollY
    //       中心 = 2H + N*H + 0.5H - scrollY = 2.5H → scrollY = N*H
    LaunchedEffect(safeSelected) {
        val target = (safeSelected * itemHeightPx).coerceAtLeast(0f)
        android.util.Log.d("WheelPicker", "LE-safeSelected[$safeSelected/${items.size}]: target=$target current=${scrollState.value} didInit=$didInitialScroll")
        if (!didInitialScroll) {
            // 初次：直接 scrollTo（不动画，避免与 isScrollInProgress 监听竞争）
            scrollState.scrollTo(target.roundToInt())
            didInitialScroll = true
        } else {
            if (abs(scrollState.value - target) > 0.5f) {
                scrollState.animateScrollTo(target.roundToInt())
            }
        }
    }

    // 根据当前 scrollPx 反推"最接近居中的 item index"
    val currentIndex by remember {
        derivedStateOf {
            val v = scrollState.value
            (v / itemHeightPx).roundToInt().coerceIn(0, items.size - 1)
        }
    }

    // 滚动停止时 snap 到最近的 item
    LaunchedEffect(scrollState.isScrollInProgress) {
        android.util.Log.d("WheelPicker", "LE-scroll[$safeSelected/${items.size}]: isScrolling=${scrollState.isScrollInProgress} value=${scrollState.value}")
        if (!scrollState.isScrollInProgress) {
            val v = scrollState.value
            val targetIndex = (v / itemHeightPx).roundToInt().coerceIn(0, items.size - 1)
            val targetPx = (targetIndex * itemHeightPx).coerceAtLeast(0f)
            if (abs(scrollState.value - targetPx) > 0.5f) {
                scrollState.animateScrollTo(targetPx.roundToInt())
            }
        }
    }

    // 当 currentIndex 与 已知 safeSelected 不一致（用户拖动了），把变更同步到外部
    LaunchedEffect(currentIndex) {
        if (currentIndex != safeSelected) {
            onSelectedIndexChange(currentIndex)
        }
    }

    Column(modifier = modifier) {
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(listHeight)
                    .clip(RoundedCornerShape(14.dp))
            ) {
                // 顶/底渐变遮罩（让边缘看起来淡出）
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        .align(Alignment.TopCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0f)
                                )
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0f),
                                    MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        )
                )

                // 可滚动 Column：每项一个 Box
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    // 顶部 padding: centerIndexOffset 个 item 的高度
                    Spacer(modifier = Modifier.height(itemHeight * centerIndexOffset))
                    items.forEachIndexed { index, text ->
                        WheelItem(
                            text = text,
                            itemHeight = itemHeight,
                            isCenter = index == safeSelected
                        )
                    }
                    Spacer(modifier = Modifier.height(itemHeight * centerIndexOffset))
                }

                // 中间高亮条（最后绘制，盖在滚动内容之上）
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .height(itemHeight)
                        .padding(horizontal = 8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(WarmAmber.copy(alpha = 0.22f))
                )
            }
        }
    }
}

@Composable
private fun WheelItem(
    text: String,
    itemHeight: Dp,
    isCenter: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(itemHeight)
            .clickable { /* 不可直接改 index；交给 Column 的滚动+ LaunchedEffect */ },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (isCenter) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = if (isCenter) 18.sp else 14.sp
            ),
            color = if (isCenter) WarmAmber
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                onClick = { expanded = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = selectedItem,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.alpha(if (expanded) 1f else 0.7f)
                    )
                }
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                items.forEach { item ->
                    val isSelected = item == selectedItem
                    DropdownMenuItem(
                        text = {
                            Text(
                                item,
                                color = if (isSelected) WarmAmber else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        },
                        modifier = Modifier.background(
                            if (isSelected) WarmAmber.copy(alpha = 0.08f) else Color.Transparent
                        )
                    )
                }
            }
        }
    }
}
