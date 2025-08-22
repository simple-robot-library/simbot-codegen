package love.forte.simbot.codegen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import love.forte.simbot.codegen.gen.view.GradleSettingsView
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.preloadFont
import simbot_codegen.composeapp.generated.resources.LXGWNeoXiHeiScreen
import simbot_codegen.composeapp.generated.resources.Res

@Composable
fun App() {
    @OptIn(ExperimentalResourceApi::class)
    val lxgwNeo by preloadFont(resource = Res.font.LXGWNeoXiHeiScreen)
    val fm = lxgwNeo?.let { FontFamily(it) }

    // 创建主题状态管理
    var colorMode by remember { mutableStateOf(ColorMode.LIGHT) }
    val appContext = remember(colorMode) {
        AppContext(
            colorMode = colorMode,
            toggleColorMode = { colorMode = colorMode.toggle() }
        )
    }

    // 使用带动画过渡的颜色方案
    val colorScheme = rememberAnimatedColorScheme(colorMode)

    AnimatedContent(fm) { fm ->
        if (fm == null) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    Spacer(Modifier.padding(8.dp))
                    Text("Font loading...")
                }
            }
        } else {
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

}
