package love.forte.simbot.codegen

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontFamily
import love.forte.simbot.codegen.gen.view.GradleSettingsView
import love.forte.simbot.codegen.theme.rememberThemeState
import love.forte.simbot.codegen.theme.rememberOptimizedAnimatedColorScheme
import love.forte.simbot.codegen.theme.rememberSciFiThemeEffects
import love.forte.simbot.codegen.theme.withFontFamily
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.preloadFont
import simbot_codegen.composeapp.generated.resources.LXGWNeoXiHeiScreen
import simbot_codegen.composeapp.generated.resources.Res

// External JavaScript function declarations
external fun notifyFontLoadingStart()
external fun notifyFontLoadingComplete()

// Theme management now handled by ThemeManager utility

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

    // 使用优化的主题状态管理
    val (colorMode, toggleColorMode) = rememberThemeState()
    val appContext = remember(colorMode) {
        AppContext(
            colorMode = colorMode,
            toggleColorMode = toggleColorMode
        )
    }

    // 使用超级优化的单一动画颜色方案，带科幻效果
    val colorScheme = rememberOptimizedAnimatedColorScheme(colorMode, useSciFiEffects = true)
    
    if(fm != null) {
        CompositionLocalProvider(LocalAppContext provides appContext) {
            MaterialTheme(
                colorScheme = colorScheme,
                typography = MaterialTheme.typography.withFontFamily(fm)
            ) {
                GradleSettingsView()
            }
        }
    }
}
