package love.forte.simbot.codegen.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import love.forte.simbot.codegen.ColorMode
import love.forte.simbot.codegen.LocalAppContext

/**
 * 主题切换按钮组件
 * 提供光滑的动画过渡和直观的图标切换
 */
@Composable
fun ThemeToggleButton(
    modifier: Modifier = Modifier
) {
    val appContext = LocalAppContext.current
    
    // 图标和描述根据当前主题模式决定
    val (icon, contentDescription) = when (appContext.colorMode) {
        ColorMode.LIGHT -> Icons.Default.DarkMode to "切换到暗色主题"
        ColorMode.DARK -> Icons.Default.LightMode to "切换到亮色主题"
    }
    
    // 旋转动画，增加视觉反馈
    val rotationAngle by animateFloatAsState(
        targetValue = when (appContext.colorMode) {
            ColorMode.LIGHT -> 0f
            ColorMode.DARK -> 360f
        },
        animationSpec = tween(durationMillis = 300),
        label = "iconRotation"
    )
    
    IconButton(
        onClick = appContext.toggleColorMode,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier
                .size(24.dp)
                .rotate(rotationAngle),
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}
