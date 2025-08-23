package love.forte.simbot.codegen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
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

    // 为每个颜色添加动画过渡 - 使用弹性动画实现更自然的过渡效果
    val animationSpec = spring<Color>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessHigh
    )

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
    val animatedSurfaceVariant by animateColorAsState(
        targetColors.surfaceVariant,
        animationSpec,
        label = "surfaceVariant"
    )
    val animatedPrimaryContainer by animateColorAsState(
        targetColors.primaryContainer,
        animationSpec,
        label = "primaryContainer"
    )
    val animatedSecondaryContainer by animateColorAsState(
        targetColors.secondaryContainer,
        animationSpec,
        label = "secondaryContainer"
    )

    // 为剩余的颜色属性添加动画
    val animatedOnPrimaryContainer by animateColorAsState(
        targetColors.onPrimaryContainer,
        animationSpec,
        label = "onPrimaryContainer"
    )
    val animatedInversePrimary by animateColorAsState(
        targetColors.inversePrimary,
        animationSpec,
        label = "inversePrimary"
    )
    val animatedOnSecondaryContainer by animateColorAsState(
        targetColors.onSecondaryContainer,
        animationSpec,
        label = "onSecondaryContainer"
    )
    val animatedTertiaryContainer by animateColorAsState(
        targetColors.tertiaryContainer,
        animationSpec,
        label = "tertiaryContainer"
    )
    val animatedOnTertiaryContainer by animateColorAsState(
        targetColors.onTertiaryContainer,
        animationSpec,
        label = "onTertiaryContainer"
    )
    val animatedOnSurfaceVariant by animateColorAsState(
        targetColors.onSurfaceVariant,
        animationSpec,
        label = "onSurfaceVariant"
    )
    val animatedSurfaceTint by animateColorAsState(targetColors.surfaceTint, animationSpec, label = "surfaceTint")
    val animatedInverseSurface by animateColorAsState(
        targetColors.inverseSurface,
        animationSpec,
        label = "inverseSurface"
    )
    val animatedInverseOnSurface by animateColorAsState(
        targetColors.inverseOnSurface,
        animationSpec,
        label = "inverseOnSurface"
    )
    val animatedOnError by animateColorAsState(targetColors.onError, animationSpec, label = "onError")
    val animatedErrorContainer by animateColorAsState(
        targetColors.errorContainer,
        animationSpec,
        label = "errorContainer"
    )
    val animatedOnErrorContainer by animateColorAsState(
        targetColors.onErrorContainer,
        animationSpec,
        label = "onErrorContainer"
    )
    val animatedOutline by animateColorAsState(targetColors.outline, animationSpec, label = "outline")
    val animatedOutlineVariant by animateColorAsState(
        targetColors.outlineVariant,
        animationSpec,
        label = "outlineVariant"
    )
    val animatedScrim by animateColorAsState(targetColors.scrim, animationSpec, label = "scrim")

    return ColorScheme(
        primary = animatedPrimary,
        onPrimary = animatedOnPrimary,
        primaryContainer = animatedPrimaryContainer,
        onPrimaryContainer = animatedOnPrimaryContainer,
        inversePrimary = animatedInversePrimary,
        secondary = animatedSecondary,
        onSecondary = animatedOnSecondary,
        secondaryContainer = animatedSecondaryContainer,
        onSecondaryContainer = animatedOnSecondaryContainer,
        tertiary = animatedTertiary,
        onTertiary = animatedOnTertiary,
        tertiaryContainer = animatedTertiaryContainer,
        onTertiaryContainer = animatedOnTertiaryContainer,
        background = animatedBackground,
        onBackground = animatedOnBackground,
        surface = animatedSurface,
        onSurface = animatedOnSurface,
        surfaceVariant = animatedSurfaceVariant,
        onSurfaceVariant = animatedOnSurfaceVariant,
        surfaceTint = animatedSurfaceTint,
        inverseSurface = animatedInverseSurface,
        inverseOnSurface = animatedInverseOnSurface,
        error = animatedError,
        onError = animatedOnError,
        errorContainer = animatedErrorContainer,
        onErrorContainer = animatedOnErrorContainer,
        outline = animatedOutline,
        outlineVariant = animatedOutlineVariant,
        scrim = animatedScrim,
        surfaceBright = targetColors.surfaceBright,
        surfaceDim = targetColors.surfaceDim,
        surfaceContainer = targetColors.surfaceContainer,
        surfaceContainerHigh = targetColors.surfaceContainerHigh,
        surfaceContainerHighest = targetColors.surfaceContainerHighest,
        surfaceContainerLow = targetColors.surfaceContainerLow,
        surfaceContainerLowest = targetColors.surfaceContainerLowest
    )
}
