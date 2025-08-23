package love.forte.simbot.codegen.theme

import androidx.compose.runtime.*
import kotlinx.browser.localStorage
import love.forte.simbot.codegen.ColorMode
import org.w3c.dom.get
import org.w3c.dom.set

/**
 * Theme management system - extracted from App.kt for better maintainability
 * Handles theme persistence and state management
 */

private const val THEME_PREFERENCE_KEY = "simbot_codegen_theme_preference"

/**
 * Theme manager for handling theme persistence and state
 */
object ThemeManager {
    
    /**
     * Load theme preference from localStorage with error handling
     */
    fun loadThemePreference(): ColorMode {
        return try {
            val savedTheme = localStorage[THEME_PREFERENCE_KEY]
            when (savedTheme) {
                "DARK" -> ColorMode.DARK
                "LIGHT" -> ColorMode.LIGHT
                else -> ColorMode.LIGHT // Default to light theme
            }
        } catch (e: Exception) {
            ColorMode.LIGHT // Fallback to light theme on error
        }
    }
    
    /**
     * Save theme preference to localStorage with silent error handling
     */
    fun saveThemePreference(colorMode: ColorMode) {
        try {
            localStorage[THEME_PREFERENCE_KEY] = colorMode.name
        } catch (e: Exception) {
            // Silent failure - doesn't affect user experience
        }
    }
}

/**
 * Composable for theme state management with persistence
 * Returns theme state and toggle function
 */
@Composable
fun rememberThemeState(): Pair<ColorMode, () -> Unit> {
    var colorMode by remember { mutableStateOf(ThemeManager.loadThemePreference()) }
    
    val toggleColorMode = remember {
        {
            val newColorMode = colorMode.toggle()
            colorMode = newColorMode
            ThemeManager.saveThemePreference(newColorMode)
        }
    }
    
    return colorMode to toggleColorMode
}

/**
 * Extension function for ColorMode to toggle between light and dark
 */
fun ColorMode.toggle(): ColorMode = when (this) {
    ColorMode.LIGHT -> ColorMode.DARK
    ColorMode.DARK -> ColorMode.LIGHT
}