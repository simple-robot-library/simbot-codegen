package love.forte.simbot.codegen.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily

/**
 * Typography utilities - extracted from App.kt to eliminate repetitive code
 * Provides efficient typography configuration following DRY principles
 */

/**
 * Extension function to apply a font family to all typography variants efficiently
 * Eliminates the need for 16 repetitive .copy() calls in App.kt
 * 
 * Following Linus Torvalds' "good taste" principle: eliminate repetitive patterns
 * and special cases through better abstraction.
 */
fun Typography.withFontFamily(fontFamily: FontFamily?): Typography {
    return if (fontFamily != null) {
        copy(
            displayLarge = displayLarge.copy(fontFamily = fontFamily),
            displayMedium = displayMedium.copy(fontFamily = fontFamily),
            displaySmall = displaySmall.copy(fontFamily = fontFamily),
            headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
            headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
            headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
            titleLarge = titleLarge.copy(fontFamily = fontFamily),
            titleMedium = titleMedium.copy(fontFamily = fontFamily),
            titleSmall = titleSmall.copy(fontFamily = fontFamily),
            bodyLarge = bodyLarge.copy(fontFamily = fontFamily),
            bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
            bodySmall = bodySmall.copy(fontFamily = fontFamily),
            labelLarge = labelLarge.copy(fontFamily = fontFamily),
            labelMedium = labelMedium.copy(fontFamily = fontFamily),
            labelSmall = labelSmall.copy(fontFamily = fontFamily),
        )
    } else {
        this // Return original typography if no font family provided
    }
}

/**
 * Alternative implementation using reflection for even cleaner code
 * This would be more "tasteful" but requires reflection support in Wasm/JS
 * Currently keeping the explicit version for better performance and compatibility
 */

/**
 * Creates a typography configuration with custom font family
 * Provides a more functional approach to typography configuration
 */
fun createTypographyWithFont(
    baseTypography: Typography,
    fontFamily: FontFamily?
): Typography = baseTypography.withFontFamily(fontFamily)