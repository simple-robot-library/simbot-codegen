package love.forte.simbot.codegen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import kotlinx.browser.localStorage
import love.forte.simbot.codegen.gen.view.GradleSettingsView
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.preloadFont
import org.w3c.dom.get
import org.w3c.dom.set
import simbot_codegen.composeapp.generated.resources.JetBrainsMono_Medium
import simbot_codegen.composeapp.generated.resources.LXGWNeoXiHeiScreen
import simbot_codegen.composeapp.generated.resources.Res

// External JavaScript function declarations
external fun notifyFontLoadingStart()
external fun notifyFontLoadingComplete()

private const val THEME_PREFERENCE_KEY = "simbot_codegen_theme_preference"

/**
 * 从本地存储加载主题偏好设置
 */
private fun loadThemePreference(): ColorMode {
    return try {
        val savedTheme = localStorage[THEME_PREFERENCE_KEY]
        when (savedTheme) {
            "DARK" -> ColorMode.DARK
            "LIGHT" -> ColorMode.LIGHT
            else -> ColorMode.LIGHT // 默认亮色主题
        }
    } catch (e: Exception) {
        ColorMode.LIGHT // 发生错误时使用默认主题
    }
}

/**
 * 保存主题偏好设置到本地存储
 */
private fun saveThemePreference(colorMode: ColorMode) {
    try {
        localStorage[THEME_PREFERENCE_KEY] = colorMode.name
    } catch (e: Exception) {
        // 保存失败时静默处理，不影响用户体验
    }
}

@Composable
fun App() {
    @OptIn(ExperimentalResourceApi::class)
    val lxgwNeo by preloadFont(resource = Res.font.LXGWNeoXiHeiScreen)
    val fm = lxgwNeo?.let { FontFamily(it) }

    // 通知字体加载状态
    LaunchedEffect(Unit) {
        // 应用启动时通知开始加载字体
        notifyFontLoadingStart()
    }

    LaunchedEffect(fm) {
        if (fm != null) {
            // 字体加载完成时通知
            notifyFontLoadingComplete()
        }
    }

    // 创建主题状态管理，从本地存储加载保存的主题偏好
    var colorMode by remember { mutableStateOf(loadThemePreference()) }
    val appContext = remember(colorMode) {
        AppContext(
            colorMode = colorMode,
            toggleColorMode = {
                val newColorMode = colorMode.toggle()
                colorMode = newColorMode
                saveThemePreference(newColorMode)
            }
        )
    }

    // 使用带动画过渡的颜色方案
    val colorScheme = rememberAnimatedColorScheme(colorMode)

    AnimatedVisibility(fm != null) {
        CompositionLocalProvider(LocalAppContext provides appContext) {
            MaterialTheme(
                colorScheme = colorScheme,
                typography = MaterialTheme.typography.copy(
                    displayLarge = MaterialTheme.typography.displayLarge.copy(fontFamily = fm),
                    displayMedium = MaterialTheme.typography.displayMedium.copy(fontFamily = fm),
                    displaySmall = MaterialTheme.typography.displaySmall.copy(fontFamily = fm),
                    headlineLarge = MaterialTheme.typography.headlineLarge.copy(fontFamily = fm),
                    headlineMedium = MaterialTheme.typography.headlineMedium.copy(fontFamily = fm),
                    headlineSmall = MaterialTheme.typography.headlineSmall.copy(fontFamily = fm),
                    titleLarge = MaterialTheme.typography.titleLarge.copy(fontFamily = fm),
                    titleMedium = MaterialTheme.typography.titleMedium.copy(fontFamily = fm),
                    titleSmall = MaterialTheme.typography.titleSmall.copy(fontFamily = fm),
                    bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontFamily = fm),
                    bodyMedium = MaterialTheme.typography.bodyMedium.copy(fontFamily = fm),
                    bodySmall = MaterialTheme.typography.bodySmall.copy(fontFamily = fm),
                    labelLarge = MaterialTheme.typography.labelLarge.copy(fontFamily = fm),
                    labelMedium = MaterialTheme.typography.labelMedium.copy(fontFamily = fm),
                    labelSmall = MaterialTheme.typography.labelSmall.copy(fontFamily = fm),
                )
            ) {
                GradleSettingsView()
            }
        }
    }
}
