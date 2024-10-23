package love.forte.simbot.codegen

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import love.forte.simbot.codegen.filesaver.saveAs
import love.forte.simbot.codegen.gen.GradleProjectViewModel
import love.forte.simbot.codegen.gen.SimbotComponent
import love.forte.simbot.codegen.gen.SimbotComponentWithVersion
import love.forte.simbot.codegen.gen.doGenerate
import love.forte.simbot.codegen.gen.view.GradleSettingsView
import love.forte.simbot.codegen.jszip.JsZipFileGenerateOptions
import org.jetbrains.compose.resources.Font
import org.w3c.files.Blob
import simbot_codegen.composeapp.generated.resources.LXGWNeoXiHeiScreen
import simbot_codegen.composeapp.generated.resources.Res


@Composable
fun App() {
    val fm = FontFamily(Font(Res.font.LXGWNeoXiHeiScreen))

    MaterialTheme(
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
        // var showContent by remember { mutableStateOf(false) }
        // Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        //     Button(onClick = {
        //         showContent = !showContent
        //         scope.launch { doDownload(gradleModel) }
        //     }) {
        //         Text("Click me!")
        //     }
        //
        //     AnimatedVisibility(showContent) {
        //         val greeting = remember { Greeting().greet() }
        //         Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        //             Image(painterResource(Res.drawable.compose_multiplatform), null)
        //             Text("Compose: $greeting")
        //         }
        //     }
        // }
    }
}

private suspend fun doDownload(gradleModel: GradleProjectViewModel) {
    gradleModel.components.addAll(SimbotComponent.entries.map { SimbotComponentWithVersion(it) })

    val zip = doGenerate(gradleModel)
    log(zip)

    val opt = JsZipFileGenerateOptions("blob")
    log(opt)

    val promise = zip.generateAsync(opt)

    promise.then { blob ->
        log(blob)
        saveAs(blob.unsafeCast<Blob>(), "file.zip")
        null
    }

    // val blob = promise.await<JsAny>().unsafeCast<Blob>()
    // log(blob)
    // saveAs(blob.unsafeCast<Blob>(), "zip.zip")
}

internal fun log(any: JsAny?) {
    js("console.log(any)")
}
