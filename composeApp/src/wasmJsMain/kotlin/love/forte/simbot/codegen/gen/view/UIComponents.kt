package love.forte.simbot.codegen.gen.view

import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.browser.window

/**
 * 定制化的输入框组件，提供统一的样式和交互体验
 */
@Composable
fun EnhancedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    placeholder: String? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    singleLine: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    val windowSize = rememberWindowSize()
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderColor by animateColorAsState(
        targetValue = when {
            isError -> MaterialTheme.colorScheme.error
            isFocused -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
        },
        animationSpec = tween(durationMillis = 200),
        label = "边框颜色动画"
    )

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isFocused -> MaterialTheme.colorScheme.surfaceVariant // .copy(alpha = 0.3f)
            else -> MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(durationMillis = 200),
        label = "背景颜色动画"
    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        label = {
            Text(
                text = label,
                fontWeight = if (isFocused) FontWeight.Medium else FontWeight.Normal,
                // 在移动设备上使用更小的字体
                fontSize = when (windowSize) {
                    WindowSize.Mobile -> 14.sp
                    else -> 16.sp
                }
            )
        },
        placeholder = placeholder?.let { { Text(it) } },
        supportingText = supportingText,
        isError = isError,
        singleLine = singleLine,
        enabled = enabled,
        interactionSource = interactionSource,
        trailingIcon = trailingIcon,
        leadingIcon = leadingIcon,
        shape = RoundedCornerShape(
            // 在移动设备上使用更小的圆角
            when (windowSize) {
                WindowSize.Mobile -> 8.dp
                else -> 12.dp
            }
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = borderColor,
            unfocusedBorderColor = borderColor.copy(alpha = 0.7f),
            focusedContainerColor = backgroundColor,
            unfocusedContainerColor = backgroundColor,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            errorBorderColor = MaterialTheme.colorScheme.error,
            errorContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f),
            errorLabelColor = MaterialTheme.colorScheme.error,
            errorCursorColor = MaterialTheme.colorScheme.error,
            errorSupportingTextColor = MaterialTheme.colorScheme.error,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        ),
    )
}

/**
 * 带动画效果的搜索图标组件
 */
@Composable
fun SearchingIcon(
    initialColor: Color = LocalContentColor.current,
    targetColor: Color = LocalContentColor.current.copy(alpha = .2f),
    animationSpec: InfiniteRepeatableSpec<Color> = infiniteRepeatable(
        tween(durationMillis = 600, delayMillis = 200),
        repeatMode = RepeatMode.Reverse
    ),
    label: String = "SearchIconColorAnimation"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "SearchIconTransition")
    val color by infiniteTransition.animateColor(
        initialValue = initialColor,
        targetValue = targetColor,
        animationSpec = animationSpec,
        label = label
    )
    val size by infiniteTransition.animateFloat(
        initialValue = 24f,
        targetValue = 28f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 800, delayMillis = 100),
            repeatMode = RepeatMode.Reverse
        ),
        label = "IconSizeAnimation"
    )

    Icon(
        Icons.Outlined.Search,
        "Searching",
        tint = color,
        modifier = Modifier.size(size.dp)
    )
}

/**
 * 记住当前窗口大小的 Composable 函数
 */
@Composable
fun rememberWindowSize(): WindowSize {
    var windowSize by remember { mutableStateOf(WindowSize.Desktop) }

    DisposableEffect(Unit) {
        val resizeListener: (JsAny?) -> Unit = {
            val width = window.innerWidth
            windowSize = when {
                width < 600 -> WindowSize.Mobile
                width < 840 -> WindowSize.Tablet
                else -> WindowSize.Desktop
            }
        }

        // 初始化窗口大小
        resizeListener(null)

        // 添加窗口调整大小的监听器
        window.addEventListener("resize", resizeListener)

        // 清理监听器
        onDispose {
            window.removeEventListener("resize", resizeListener)
        }
    }

    return windowSize
}

/**
 * 窗口大小枚举类
 */
enum class WindowSize {
    Mobile, Tablet, Desktop
}