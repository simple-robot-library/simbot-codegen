package love.forte.simbot.codegen.gen.view

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import love.forte.simbot.codegen.gen.view.preview.ZipPreviewDialog
import web.blob.Blob
import web.console.console

/**
 * 下载和预览按钮组件，用于生成、预览和下载项目
 */
@OptIn(ExperimentalWasmJsInterop::class)
@Composable
fun DoDownload(
    project: GradleProjectViewModel,
    loadingCounter: LoadingCounter,
    windowSize: WindowSize,
) {
    val scope = rememberCoroutineScope()
    var showPreview by remember { mutableStateOf(false) }
    var previewZip by remember { mutableStateOf<jszip.JSZip?>(null) }

    // 生成 ZIP 的通用逻辑
    suspend fun generateZip(): jszip.JSZip {
        val generatorFactory = LanguageAndFrameworkBasedGeneratorFactory(
            projectGeneratorFactory = { GradleProjectGeneratorImpl() },
            kotlinSourceGeneratorFactory = { KotlinSourceCodeGeneratorImpl() },
            javaSourceGeneratorFactory = { JavaSourceCodeGeneratorImpl() },
            springConfigGeneratorFactory = { SpringConfigurationGeneratorImpl() },
            coreConfigGeneratorFactory = { CoreConfigurationGeneratorImpl() }
        )
        val bridge = ViewModelBridge(generatorFactory)
        return bridge.generateProject(project)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 按钮组：预览和下载
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 预览按钮
            PreviewButton(
                enabled = !loadingCounter.hasLoading,
                windowSize = windowSize,
                onClick = {
                    loadingCounter.inc()
                    scope.launch {
                        kotlin.runCatching {
                            previewZip = generateZip()
                            showPreview = true
                        }.onFailure { err ->
                            err.printStackTrace()
                            console.error("生成预览失败".toJsString(), err.toJsErrorLike())
                            window.alert("生成预览失败QAQ")
                        }
                    }.invokeOnCompletion { loadingCounter.dec() }
                },
                modifier = Modifier.weight(1f)
            )

            // 下载按钮
            DownloadButton(
                enabled = !loadingCounter.hasLoading,
                windowSize = windowSize,
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
                modifier = Modifier.weight(1f)
            )
        }
    }

    // 预览对话框
    if (showPreview && previewZip != null) {
        ZipPreviewDialog(
            zip = previewZip!!,
            windowSize = windowSize,
            onDismiss = {
                showPreview = false
                previewZip = null
            }
        )
    }
}

/**
 * 预览按钮组件
 */
@Composable
private fun PreviewButton(
    enabled: Boolean,
    windowSize: WindowSize,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(modifier = modifier) {
        OutlinedButton(
            enabled = enabled,
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    when (windowSize) {
                        WindowSize.Mobile -> 64.dp
                        else -> 56.dp
                    }
                )
                .shadow(
                    elevation = if (isPressed) 0.dp else 2.dp,
                    shape = RoundedCornerShape(12.dp),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                ),
            shape = RoundedCornerShape(12.dp),
            interactionSource = interactionSource,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            border = BorderStroke(
                width = 2.dp,
                color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Visibility,
                    contentDescription = "预览",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "预览",
                    fontSize = if (windowSize == WindowSize.Mobile) 16.sp else 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        // Beta 标记
        BetaBadge(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 4.dp, y = (-4).dp)
        )
    }
}

/**
 * Beta 标记组件
 */
@Composable
private fun BetaBadge(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(width = 32.dp, height = 18.dp)
            .background(
                color = androidx.compose.ui.graphics.Color(0xFFFF6B35), // 橙红色背景
                shape = RoundedCornerShape(6.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Beta",
            color = androidx.compose.ui.graphics.Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

/**
 * 下载按钮组件
 */
@Composable
private fun DownloadButton(
    enabled: Boolean,
    windowSize: WindowSize,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Button(
        enabled = enabled,
        onClick = onClick,
        modifier = modifier
            .height(
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
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "下载",
                fontSize = if (windowSize == WindowSize.Mobile) 16.sp else 16.sp,
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
