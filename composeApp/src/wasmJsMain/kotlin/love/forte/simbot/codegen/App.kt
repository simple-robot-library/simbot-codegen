package love.forte.simbot.codegen

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import love.forte.simbot.codegen.gen.view.GradleSettingsView
import org.jetbrains.compose.resources.Font
import simbot_codegen.composeapp.generated.resources.LXGWNeoXiHeiScreen
import simbot_codegen.composeapp.generated.resources.Res


@Composable
fun App() {
    val fm = FontFamily(Font(Res.font.LXGWNeoXiHeiScreen))

    val darkTheme = isSystemInDarkTheme()

    val colorScheme = if (darkTheme) {
        darkColorScheme(
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
    } else {
        lightColorScheme(
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
    }


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
//
// private suspend fun doDownload(gradleModel: GradleProjectViewModel) {
//     gradleModel.components.addAll(SimbotComponent.entries.map { SimbotComponentWithVersion(it) })
//
//     val zip = doGenerate(gradleModel)
//     log(zip)
//
//     val opt = JsZipFileGenerateOptions("blob")
//     log(opt)
//
//     val promise = zip.generateAsync(opt)
//
//     promise.then { blob ->
//         log(blob)
//         saveAs(blob.unsafeCast<Blob>(), "file.zip")
//         null
//     }
//
//     // val blob = promise.await<JsAny>().unsafeCast<Blob>()
//     // log(blob)
//     // saveAs(blob.unsafeCast<Blob>(), "zip.zip")
// }
