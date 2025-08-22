package love.forte.simbot.codegen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

/**
 * 应用上下文数据类，包含全局应用状态
 */
data class AppContext(
    val colorMode: ColorMode,
    val toggleColorMode: () -> Unit
)

/**
 * 用于提供全局应用上下文的 CompositionLocal
 */
val LocalAppContext = staticCompositionLocalOf<AppContext> {
    error("LocalAppContext not provided")
}

/**
 * 颜色模式枚举
 */
enum class ColorMode {
    DARK,
    LIGHT;
    
    fun toggle(): ColorMode = when (this) {
        DARK -> LIGHT
        LIGHT -> DARK
    }
}

// 定义基础颜色
val lightColors = lightColorScheme(
    primary = Color(0xFF3B7DD8),           // 优雅的蓝色
    secondary = Color(0xFF4DB6A5),         // 平衡的绿松石色
    tertiary = Color(0xFFEF9265),          // 温和的橙色
    background = Color(0xFFF8F8F8),        // 温和的白色背景
    surface = Color(0xFFFFFFFF),           // 纯白色表面
    error = Color(0xFFD32F2F),             // 标准错误色
    onPrimary = Color(0xFFFFFFFF),         // 主色上的文本
    onSecondary = Color(0xFFFFFFFF),       // 次要色上的文本
    onTertiary = Color(0xFFFFFFFF),        // 第三色上的文本
    onBackground = Color(0xFF212121),      // 背景上的文本
    onSurface = Color(0xFF212121),         // 表面上的文本
    surfaceVariant = Color(0xFFE6E6E6),    // 表面变体
    primaryContainer = Color(0xFFD4E3F8),  // 主色容器
    secondaryContainer = Color(0xFFD4F0EB) // 次要色容器
)

val darkColors = darkColorScheme(
    primary = Color(0xFF5B9EE1),           // 柔和的蓝色
    secondary = Color(0xFF80CBBF),         // 淡绿蓝色
    tertiary = Color(0xFFEFB196),          // 淡橙色
    background = Color(0xFF1A1A1A),        // 稍微柔和的黑色
    surface = Color(0xFF252525),           // 柔和的暗色表面
    error = Color(0xFFE57373),             // 淡红色错误提示
    onPrimary = Color(0xFFF5F5F5),         // 主色上的文本
    onSecondary = Color(0xFF121212),       // 次要色上的文本
    onTertiary = Color(0xFF121212),        // 第三色上的文本
    onBackground = Color(0xFFE0E0E0),      // 背景上的文本
    onSurface = Color(0xFFE0E0E0),         // 表面上的文本
    surfaceVariant = Color(0xFF3A3A3A),    // 表面变体
    primaryContainer = Color(0xFF2F4D6F),  // 主色容器
    secondaryContainer = Color(0xFF396458) // 次要色容器
)

/**
 * 创建带动画过渡的颜色方案
 */
@Composable
fun rememberAnimatedColorScheme(colorMode: ColorMode): ColorScheme {

    
    val targetColors = when (colorMode) {
        ColorMode.LIGHT -> lightColors
        ColorMode.DARK -> darkColors
    }
    
    // 为每个颜色添加动画过渡
    val animationDuration = 300
    val animationSpec = tween<Color>(durationMillis = animationDuration)
    
    val animatedPrimary by animateColorAsState(targetColors.primary, animationSpec, label = "primary")
    val animatedSecondary by animateColorAsState(targetColors.secondary, animationSpec, label = "secondary")
    val animatedTertiary by animateColorAsState(targetColors.tertiary, animationSpec, label = "tertiary")
    val animatedBackground by animateColorAsState(targetColors.background, animationSpec, label = "background")
    val animatedSurface by animateColorAsState(targetColors.surface, animationSpec, label = "surface")
    val animatedError by animateColorAsState(targetColors.error, animationSpec, label = "error")
    val animatedOnPrimary by animateColorAsState(targetColors.onPrimary, animationSpec, label = "onPrimary")
    val animatedOnSecondary by animateColorAsState(targetColors.onSecondary, animationSpec, label = "onSecondary")
    val animatedOnTertiary by animateColorAsState(targetColors.onTertiary, animationSpec, label = "onTertiary")
    val animatedOnBackground by animateColorAsState(targetColors.onBackground, animationSpec, label = "onBackground")
    val animatedOnSurface by animateColorAsState(targetColors.onSurface, animationSpec, label = "onSurface")
    val animatedSurfaceVariant by animateColorAsState(targetColors.surfaceVariant, animationSpec, label = "surfaceVariant")
    val animatedPrimaryContainer by animateColorAsState(targetColors.primaryContainer, animationSpec, label = "primaryContainer")
    val animatedSecondaryContainer by animateColorAsState(targetColors.secondaryContainer, animationSpec, label = "secondaryContainer")
    
    return ColorScheme(
        primary = animatedPrimary,
        onPrimary = animatedOnPrimary,
        primaryContainer = animatedPrimaryContainer,
        onPrimaryContainer = targetColors.onPrimaryContainer,
        inversePrimary = targetColors.inversePrimary,
        secondary = animatedSecondary,
        onSecondary = animatedOnSecondary,
        secondaryContainer = animatedSecondaryContainer,
        onSecondaryContainer = targetColors.onSecondaryContainer,
        tertiary = animatedTertiary,
        onTertiary = animatedOnTertiary,
        tertiaryContainer = targetColors.tertiaryContainer,
        onTertiaryContainer = targetColors.onTertiaryContainer,
        background = animatedBackground,
        onBackground = animatedOnBackground,
        surface = animatedSurface,
        onSurface = animatedOnSurface,
        surfaceVariant = animatedSurfaceVariant,
        onSurfaceVariant = targetColors.onSurfaceVariant,
        surfaceTint = targetColors.surfaceTint,
        inverseSurface = targetColors.inverseSurface,
        inverseOnSurface = targetColors.inverseOnSurface,
        error = animatedError,
        onError = targetColors.onError,
        errorContainer = targetColors.errorContainer,
        onErrorContainer = targetColors.onErrorContainer,
        outline = targetColors.outline,
        outlineVariant = targetColors.outlineVariant,
        scrim = targetColors.scrim,
        surfaceBright = targetColors.surfaceBright,
        surfaceDim = targetColors.surfaceDim,
        surfaceContainer = targetColors.surfaceContainer,
        surfaceContainerHigh = targetColors.surfaceContainerHigh,
        surfaceContainerHighest = targetColors.surfaceContainerHighest,
        surfaceContainerLow = targetColors.surfaceContainerLow,
        surfaceContainerLowest = targetColors.surfaceContainerLowest
    )
}
