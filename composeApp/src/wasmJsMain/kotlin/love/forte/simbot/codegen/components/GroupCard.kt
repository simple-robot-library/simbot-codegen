package love.forte.simbot.codegen.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * 统一的分组卡片组件，用于将表单内容按功能分组展示
 * 提供现代化的卡片样式，支持标题和内容区域
 *
 * @param title 分组标题
 * @param modifier 修饰符
 * @param subtitle 可选的副标题
 * @param content 分组内容
 */
@Composable
fun GroupCard(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    
    // 鼠标位置状态，用于动态调整毛玻璃效果的渐变中心
    var mousePosition by remember { mutableStateOf<Offset?>(null) }

    val cardBorderColor by animateColorAsState(
        targetValue = if (isHovered) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        } else {
            MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
        },
        label = "borderColor"
    )

    val cardContainerColor by animateColorAsState(
        targetValue = if (isHovered) {
            // 当启用毛玻璃效果时，使用透明背景让效果显现
            MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        label = "cardContainerColor"
    )

    val cardElevationValue by animateDpAsState(
        targetValue = if (isHovered) 2.dp else 0.dp,
        label = "cardElevation"
    )
    val cardShape = RoundedCornerShape(16.dp) // 稍微增大圆角以获得更现代的外观
    
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .optimizedFrostedGlass(
                isActive = isHovered, 
                intensity = 0.8f, 
                shape = cardShape,
                mousePosition = mousePosition
            )
            .pointerInput(isHovered) {
                if (isHovered) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            event.changes.firstOrNull()?.let { change ->
                                mousePosition = change.position
                            }
                        }
                    }
                } else {
                    mousePosition = null
                }
            }
            .hoverable(interactionSource),
        shape = cardShape,
        border = BorderStroke(
            width = 1.dp,
            color = cardBorderColor
        ),
        colors = CardDefaults.outlinedCardColors(
            containerColor = cardContainerColor
        ),
        elevation = CardDefaults.outlinedCardElevation(
            defaultElevation = cardElevationValue
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp), // 增加内边距以获得更现代的外观
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 标题区域
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 内容区域
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                content = content
            )
        }
    }
}

/**
 * 响应式的分组网格，用于在不同屏幕尺寸下展示分组
 *
 * @param modifier 修饰符
 * @param windowSize 窗口尺寸
 * @param content 分组内容
 */
@Composable
fun GroupGrid(
    modifier: Modifier = Modifier,
    windowSize: WindowSize,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(
            when (windowSize) {
                WindowSize.Mobile -> 16.dp
                WindowSize.Tablet -> 20.dp
                WindowSize.Desktop -> 24.dp
            }
        ),
        content = content
    )
}
