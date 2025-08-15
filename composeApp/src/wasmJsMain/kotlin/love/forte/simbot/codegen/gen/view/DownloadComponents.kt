package love.forte.simbot.codegen.gen.view

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import js.errors.toJsErrorLike
import js.objects.unsafeJso
import js.promise.await
import jszip.JSZipGeneratorOptions
import jszip.OutputType
import jszip.blob
import kotlinx.browser.window
import kotlinx.coroutines.launch
import love.forte.simbot.codegen.components.WindowSize
import love.forte.simbot.codegen.filesaver.saveAs
import love.forte.simbot.codegen.gen.GradleProjectViewModel
import love.forte.simbot.codegen.gen.bridge.ViewModelBridge
import love.forte.simbot.codegen.gen.core.generators.LanguageAndFrameworkBasedGeneratorFactory
import love.forte.simbot.codegen.gen.core.generators.core.CoreConfigurationGeneratorImpl
import love.forte.simbot.codegen.gen.core.generators.gradle.GradleProjectGeneratorImpl
import love.forte.simbot.codegen.gen.core.generators.java.JavaSourceCodeGeneratorImpl
import love.forte.simbot.codegen.gen.core.generators.kotlin.KotlinSourceCodeGeneratorImpl
import love.forte.simbot.codegen.gen.core.generators.spring.SpringConfigurationGeneratorImpl
import web.blob.Blob
import web.console.console

/**
 * 下载按钮组件，用于生成并下载项目
 */
@OptIn(ExperimentalWasmJsInterop::class)
@Composable
fun DoDownload(
    project: GradleProjectViewModel,
    loadingCounter: LoadingCounter,
    windowSize: WindowSize,
) {
    val scope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Button(
        enabled = !loadingCounter.hasLoading,
        onClick = {
            loadingCounter.inc()
            scope.launch {
                kotlin.runCatching {
                    doDownload(project)
                }.onFailure { err ->
                    err.printStackTrace()
                    console.error("生成失败".toJsString(), err.toJsErrorLike())
                    window.alert("生成失败QAQ")
                }
            }.invokeOnCompletion { loadingCounter.dec() }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(
                // 移动设备使用更大的高度以便于触摸
                when (windowSize) {
                    WindowSize.Mobile -> 64.dp
                    else -> 56.dp
                }
            )
            .shadow(
                elevation = if (isPressed) 0.dp else 4.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            ),
        shape = RoundedCornerShape(12.dp),
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Download,
                contentDescription = "下载",
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 8.dp)
            )

            Text(
                "生成并下载",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * 执行项目下载的挂起函数
 */
private suspend fun doDownload(
    project: GradleProjectViewModel
) {
    val name = project.projectName

    // 创建生成器工厂
    val generatorFactory = LanguageAndFrameworkBasedGeneratorFactory(
        projectGeneratorFactory = { GradleProjectGeneratorImpl() },
        kotlinSourceGeneratorFactory = { KotlinSourceCodeGeneratorImpl() },
        javaSourceGeneratorFactory = { JavaSourceCodeGeneratorImpl() },
        springConfigGeneratorFactory = { SpringConfigurationGeneratorImpl() },
        coreConfigGeneratorFactory = { CoreConfigurationGeneratorImpl() }
    )

    // 创建视图模型桥接器
    val bridge = ViewModelBridge(generatorFactory)

    // 生成项目
    val zip = bridge.generateProject(project)

    val options = unsafeJso<JSZipGeneratorOptions<Blob>> {
        type = OutputType.blob
    }

    // TODO onUpdate?
    val blob = zip.generateAsync(options).await()

    saveAs(blob, "$name.zip")
}
