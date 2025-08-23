package love.forte.simbot.codegen.theme

import androidx.compose.animation.core.*
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import love.forte.simbot.codegen.ColorMode
import love.forte.simbot.codegen.darkColors
import love.forte.simbot.codegen.lightColors
import kotlin.math.abs

/**
 * Ultra-optimized theme animation system
 * Replaces 27 separate animateColorAsState calls with a single interpolation-based animation
 * Provides smooth 60fps theme transitions with sci-fi effects
 */

/**
 * Optimized animation specification for theme transitions
 * Uses high-performance spring animation with reduced bounce for smoother feel
 */
private val themeTransitionSpec = spring<Float>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessHigh,
    visibilityThreshold = 0.001f
)

/**
 * Enhanced animation spec for sci-fi effects with subtle easing
 */
private val sciFiTransitionSpec = tween<Float>(
    durationMillis = 400,
    easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1.0f)
)

/**
 * Memory-optimized color scheme cache to prevent unnecessary allocations
 */
private class ColorSchemeCache {
    private var cachedProgress: Float = -1f
    private var cachedColorScheme: ColorScheme? = null
    private var cachedLightColors: ColorScheme? = null
    private var cachedDarkColors: ColorScheme? = null
    
    fun getColorScheme(
        progress: Float, 
        lightColors: ColorScheme, 
        darkColors: ColorScheme
    ): ColorScheme {
        // Cache color schemes to avoid repeated allocations
        if (cachedLightColors != lightColors) {
            cachedLightColors = lightColors
            cachedProgress = -1f // Invalidate cache
        }
        if (cachedDarkColors != darkColors) {
            cachedDarkColors = darkColors
            cachedProgress = -1f // Invalidate cache
        }
        
        // Return cached result if progress hasn't changed significantly
        if (abs(cachedProgress - progress) < 0.001f && cachedColorScheme != null) {
            return cachedColorScheme!!
        }
        
        // Create new interpolated color scheme
        val interpolated = interpolateColorScheme(lightColors, darkColors, progress)
        cachedProgress = progress
        cachedColorScheme = interpolated
        return interpolated
    }
}

/**
 * Ultra-fast color interpolation optimized for theme transitions
 */
@Composable
fun rememberOptimizedAnimatedColorScheme(
    colorMode: ColorMode,
    useSciFiEffects: Boolean = true
): ColorScheme {
    // Use remember to maintain cache across recompositions
    val cache = remember { ColorSchemeCache() }
    
    // Single animation value that controls the entire transition
    val progress by animateFloatAsState(
        targetValue = when (colorMode) {
            ColorMode.LIGHT -> 0f
            ColorMode.DARK -> 1f
        },
        animationSpec = if (useSciFiEffects) sciFiTransitionSpec else themeTransitionSpec,
        label = "themeProgress"
    )
    
    // Use cached interpolation to minimize allocations
    return cache.getColorScheme(progress, lightColors, darkColors)
}

/**
 * High-performance color scheme interpolation
 * Interpolates all colors in a single pass for maximum efficiency
 */
private fun interpolateColorScheme(
    lightColors: ColorScheme,
    darkColors: ColorScheme,
    progress: Float
): ColorScheme {
    return ColorScheme(
        primary = lerp(lightColors.primary, darkColors.primary, progress),
        onPrimary = lerp(lightColors.onPrimary, darkColors.onPrimary, progress),
        primaryContainer = lerp(lightColors.primaryContainer, darkColors.primaryContainer, progress),
        onPrimaryContainer = lerp(lightColors.onPrimaryContainer, darkColors.onPrimaryContainer, progress),
        inversePrimary = lerp(lightColors.inversePrimary, darkColors.inversePrimary, progress),
        secondary = lerp(lightColors.secondary, darkColors.secondary, progress),
        onSecondary = lerp(lightColors.onSecondary, darkColors.onSecondary, progress),
        secondaryContainer = lerp(lightColors.secondaryContainer, darkColors.secondaryContainer, progress),
        onSecondaryContainer = lerp(lightColors.onSecondaryContainer, darkColors.onSecondaryContainer, progress),
        tertiary = lerp(lightColors.tertiary, darkColors.tertiary, progress),
        onTertiary = lerp(lightColors.onTertiary, darkColors.onTertiary, progress),
        tertiaryContainer = lerp(lightColors.tertiaryContainer, darkColors.tertiaryContainer, progress),
        onTertiaryContainer = lerp(lightColors.onTertiaryContainer, darkColors.onTertiaryContainer, progress),
        background = lerp(lightColors.background, darkColors.background, progress),
        onBackground = lerp(lightColors.onBackground, darkColors.onBackground, progress),
        surface = lerp(lightColors.surface, darkColors.surface, progress),
        onSurface = lerp(lightColors.onSurface, darkColors.onSurface, progress),
        surfaceVariant = lerp(lightColors.surfaceVariant, darkColors.surfaceVariant, progress),
        onSurfaceVariant = lerp(lightColors.onSurfaceVariant, darkColors.onSurfaceVariant, progress),
        surfaceTint = lerp(lightColors.surfaceTint, darkColors.surfaceTint, progress),
        inverseSurface = lerp(lightColors.inverseSurface, darkColors.inverseSurface, progress),
        inverseOnSurface = lerp(lightColors.inverseOnSurface, darkColors.inverseOnSurface, progress),
        error = lerp(lightColors.error, darkColors.error, progress),
        onError = lerp(lightColors.onError, darkColors.onError, progress),
        errorContainer = lerp(lightColors.errorContainer, darkColors.errorContainer, progress),
        onErrorContainer = lerp(lightColors.onErrorContainer, darkColors.onErrorContainer, progress),
        outline = lerp(lightColors.outline, darkColors.outline, progress),
        outlineVariant = lerp(lightColors.outlineVariant, darkColors.outlineVariant, progress),
        scrim = lerp(lightColors.scrim, darkColors.scrim, progress),
        // Include all surface variants with interpolation for consistency
        surfaceBright = lerp(lightColors.surfaceBright, darkColors.surfaceBright, progress),
        surfaceDim = lerp(lightColors.surfaceDim, darkColors.surfaceDim, progress),
        surfaceContainer = lerp(lightColors.surfaceContainer, darkColors.surfaceContainer, progress),
        surfaceContainerHigh = lerp(lightColors.surfaceContainerHigh, darkColors.surfaceContainerHigh, progress),
        surfaceContainerHighest = lerp(lightColors.surfaceContainerHighest, darkColors.surfaceContainerHighest, progress),
        surfaceContainerLow = lerp(lightColors.surfaceContainerLow, darkColors.surfaceContainerLow, progress),
        surfaceContainerLowest = lerp(lightColors.surfaceContainerLowest, darkColors.surfaceContainerLowest, progress)
    )
}

/**
 * Sci-fi theme transition effects
 * Provides visual enhancement during theme switching
 */
@Composable
fun rememberSciFiThemeEffects(colorMode: ColorMode): ThemeEffectState {
    var isTransitioning by remember { mutableStateOf(false) }
    var lastColorMode by remember { mutableStateOf(colorMode) }
    
    // Detect theme transition start
    LaunchedEffect(colorMode) {
        if (lastColorMode != colorMode) {
            isTransitioning = true
            lastColorMode = colorMode
            
            // Auto-reset transition state after animation completes
            kotlinx.coroutines.delay(450) // Slightly longer than animation duration
            isTransitioning = false
        }
    }
    
    // Glow intensity animation for sci-fi effect
    val glowIntensity by animateFloatAsState(
        targetValue = if (isTransitioning) 1f else 0f,
        animationSpec = tween(200),
        label = "glowIntensity"
    )
    
    // Pulse effect for transition feedback
    val pulseScale by animateFloatAsState(
        targetValue = if (isTransitioning) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "pulseScale"
    )
    
    return ThemeEffectState(
        isTransitioning = isTransitioning,
        glowIntensity = glowIntensity,
        pulseScale = pulseScale
    )
}

/**
 * Data class for theme effect state
 */
data class ThemeEffectState(
    val isTransitioning: Boolean,
    val glowIntensity: Float,
    val pulseScale: Float
)

/**
 * Performance monitoring utilities for theme animations
 * Simplified for WASM/JS compatibility
 */
object ThemePerformanceMonitor {
    private var frameCount = 0
    
    fun recordFrame() {
        frameCount++
    }
    
    fun getFrameCount(): Int = frameCount
    
    fun reset() {
        frameCount = 0
    }
}