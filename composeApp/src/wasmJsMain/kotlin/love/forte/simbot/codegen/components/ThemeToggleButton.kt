package love.forte.simbot.codegen.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import love.forte.simbot.codegen.ColorMode
import love.forte.simbot.codegen.LocalAppContext

/**
 * 主题切换按钮组件
 * 提供精美的动画过渡和直观的视觉反馈
 * 采用现代化的切换开关设计，融合Material 3设计语言
 */
@Composable
fun ThemeToggleButton(
    modifier: Modifier = Modifier
) {
    val appContext = LocalAppContext.current
    val isDarkMode = appContext.colorMode == ColorMode.DARK
    
    // 动画状态管理
    var isPressed by remember { mutableStateOf(false) }
    
    // 切换动画 - 滑动效果
    val toggleProgress by animateFloatAsState(
        targetValue = if (isDarkMode) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "toggleProgress"
    )
    
    // 图标旋转动画 - 更加流畅的旋转
    val iconRotation by animateFloatAsState(
        targetValue = if (isDarkMode) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "iconRotation"
    )
    
    // 按压缩放动画
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "scale"
    )
    
    // 发光效果动画
    val glowIntensity by animateFloatAsState(
        targetValue = if (isPressed) 0.3f else 0.1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "glow"
    )
    
    Box(
        modifier = modifier
            .padding(16.dp)
            .width(56.dp)
            .height(32.dp)
            .scale(scale)
            .shadow(
                elevation = (4 + glowIntensity * 8).dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = if (isDarkMode) 
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) 
                else 
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(
                color = if (isDarkMode) {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                },
                shape = RoundedCornerShape(16.dp)
            )
            .clickable {
                appContext.toggleColorMode()
            }
    ) {
        // 滑动指示器背景
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            // 滑动圆形指示器
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .offset(x = (26 * toggleProgress).dp)
                    .align(Alignment.CenterStart)
                    .shadow(
                        elevation = 1.dp,
                        shape = CircleShape,
                        ambientColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                    contentDescription = if (isDarkMode) "切换到亮色主题" else "切换到暗色主题",
                    modifier = Modifier
                        .size(16.dp)
                        .rotate(iconRotation),
                    tint = if (isDarkMode) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            }
        }
    }
}
