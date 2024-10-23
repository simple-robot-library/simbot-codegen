package love.forte.simbot.codegen.gen.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.onClick
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.UrlAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import love.forte.simbot.codegen.JsDate
import love.forte.simbot.codegen.filesaver.saveAs
import love.forte.simbot.codegen.gen.COMPONENT_KOOK
import love.forte.simbot.codegen.gen.COMPONENT_OB_11
import love.forte.simbot.codegen.gen.COMPONENT_QQ
import love.forte.simbot.codegen.gen.ComponentVersion
import love.forte.simbot.codegen.gen.GradleProjectViewModel
import love.forte.simbot.codegen.gen.SIMBOT_VERSION
import love.forte.simbot.codegen.gen.SimbotComponent
import love.forte.simbot.codegen.gen.SimbotComponentWithVersion
import love.forte.simbot.codegen.gen.doGenerate
import love.forte.simbot.codegen.jszip.JsZipFileGenerateOptions
import love.forte.simbot.codegen.versions.fetchLatest
import org.w3c.files.Blob
import kotlin.time.Duration.Companion.seconds

class LoadingCounter : ViewModel() {
    private val loading = mutableIntStateOf(0)

    val count: Int
        get() = loading.value

    val hasLoading: Boolean
        get() = loading.value > 0

    fun addLoading() {
        loading.value++
    }

    fun removeLoading() {
        loading.value--
    }
}

operator fun LoadingCounter.inc(): LoadingCounter = apply { addLoading() }
operator fun LoadingCounter.dec(): LoadingCounter = apply { removeLoading() }

/**
 * Composable function to display the view for configuring Gradle project settings.
 *
 * This view allows users to manage settings related to a*/
@OptIn(ExperimentalMaterial3Api::class, ExperimentalTextApi::class, ExperimentalFoundationApi::class)
@Composable
fun GradleSettingsView(
    projectViewModel: GradleProjectViewModel = viewModel { GradleProjectViewModel() },
    loadingCounter: LoadingCounter = remember { LoadingCounter() },
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = topAppBarColors(
                    // containerColor = MaterialTheme.colorScheme.primaryContainer,
                    // titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Simbot Codegen")
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                // containerColor = MaterialTheme.colorScheme.primaryContainer,
                // contentColor = MaterialTheme.colorScheme.primary,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("© ${JsDate().getFullYear()} ")
                    val link = "https://github.com/simple-robot"
                    ClickableText(
                        buildAnnotatedString {
                            withStyle(SpanStyle(color = Color.Blue)) {
                                append("Simple Robot.")
                                pushUrlAnnotation(UrlAnnotation(link))
                            }
                        }) {
                        window.open(link, target = "_blank")
                    }
                    Text("All rights reserved.")
                }
            }
        },

        content = { innerPaddings ->
            val focusManager = LocalFocusManager.current
            val scrollState = rememberScrollState()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPaddings)
                    .padding(top = 8.dp, bottom = 8.dp)
                    .verticalScroll(scrollState)
                    .onClick {
                        focusManager.clearFocus()
                    },
                contentAlignment = Alignment.TopCenter,
            ) {
                SettingViewContent(projectViewModel, loadingCounter)
            }
        }
    )
    // Box(
    //     modifier = Modifier.fillMaxSize(),
    //     contentAlignment = androidx.compose.ui.Alignment.Center,
    // ) {
    // }
}


@Composable
private fun SettingViewContent(
    project: GradleProjectViewModel,
    loadingCounter: LoadingCounter,
) {
    Box(
        modifier = Modifier.fillMaxWidth(.45f)
    ) {
        SettingsForm(project, loadingCounter)
    }
}

@Composable
private fun SettingsForm(project: GradleProjectViewModel, loadingCounter: LoadingCounter) {
    Column(
        modifier = Modifier.focusGroup(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
    ) {
        ProjectName(project)
        ProjectPackage(project)
        LanguageSelection(project)
        SimbotVersion(project, loadingCounter)
        GradleVersion(project)
        WithSpring(project)
        ComponentSelection(project, loadingCounter)

        // Do download
        DoDownload(project, loadingCounter)
    }
}

/**
 * 此函数用于展示一个可编辑的文本字段，用以输入或修改 Gradle 项目的名称。
 * 输入框中包含了对项目名称的要求提示，并会在项目名称为空时显示错误信息。
 *
 * @param project [GradleProjectViewModel] 类型的参数，包含了项目的基本信息，如项目名称等。
 */
@Composable
private fun ProjectName(project: GradleProjectViewModel) {
    val isDanger = project.projectName.isEmpty() //  by remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        label = { Text("项目名称") },
        placeholder = { Text("你的项目名称") },
        supportingText = {
            Column {
                Text("你的项目名称。同时也会作为项目的工件名。")
                Text("建议仅包含大小写英文、数字和下划线，不以数字为开头，且长度不可大于60。")
                AnimatedVisibility(isDanger) {
                    Text("项目名称不可为空！", fontWeight = FontWeight.Bold)
                }
            }
        },
        isError = isDanger,
        singleLine = true,
        value = project.projectName,
        onValueChange = {
            project.projectName = it
        },
    )
}

/**
 * 此函数用于展示一个可编辑的文本字段，用于输入或修改 Gradle 项目的根包名。
 * 包名需遵循一定的命名规范，不符合规范时会显示错误提示。
 * 用户输入的包名将直接更新到 `GradleProjectViewModel` 中的 `projectPackage` 状态。
 *
 * @param project [GradleProjectViewModel] 类型的参数，存储了项目的所有配置信息，包括项目包名。
 */
@Composable
private fun ProjectPackage(project: GradleProjectViewModel) {
    val isDanger = !project.projectPackage.matches(Regex("[a-zA-Z][a-zA-Z0-9_]*(\\.[a-zA-Z][a-zA-Z0-9_]*)*"))

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        label = { Text("项目包") },
        placeholder = { Text("你的项目根包结构") },
        supportingText = {
            Column {
                Text(
                    "你的项目根包结构。建议符合正则 [a-zA-Z][a-zA-Z0-9_]*(\\.[a-zA-Z][a-zA-Z0-9_]*)* ，" +
                            "包结构应当至少有一层，整体长度不可大于120。"
                )
                AnimatedVisibility(isDanger) {
                    Text("项目包结构格式不匹配！", fontWeight = FontWeight.Bold)
                }
            }
        },
        isError = isDanger,
        singleLine = true,
        value = project.projectPackage,
        onValueChange = {
            project.projectPackage = it
        },
    )
}

@Composable
private fun LanguageSelection(
    project: GradleProjectViewModel,
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = true,
                    enabled = false,
                    onClick = {}
                )

                Text("Kotlin")
            }
        }
    }
    KotlinVersion(project)
}

@Composable
private fun KotlinVersion(
    project: GradleProjectViewModel,
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = project.kotlinVersion,
        onValueChange = { project.kotlinVersion = it.trim() },
        label = { Text("Kotlin 版本") },
        trailingIcon = null,
        supportingText = {
            Column {
                Text("输入一个要使用的 Kotlin 版本。请尽可能与对应的 simbot 所使用的 Kotlin 版本对应。")
            }
        }
    )
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun SimbotVersion(
    project: GradleProjectViewModel,
    loadingCounter: LoadingCounter
) {
    val simbotVersion = project.simbotVersion
    var isLoading by remember { mutableStateOf(false) }

    if (simbotVersion == null) {
        LaunchedEffect(Unit) {
            isLoading = true
            loadingCounter.inc()
            try {
                // TODO query got 'The user aborted a request.'
                val latest = fetchLatest("simple-robot", "simpler-robot")
                project.simbotVersion = latest.tagName.removePrefix("v")
            } catch (e: Throwable) {
                e.printStackTrace()
                project.simbotVersion = SIMBOT_VERSION.version
            } finally {
                isLoading = false
                loadingCounter.dec()
            }
        }
    }

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        enabled = !isLoading,
        value = simbotVersion ?: "",
        onValueChange = { project.simbotVersion = it.trim() },
        label = {
            Text("simbot版本")
        },
        trailingIcon = {
            AnimatedVisibility(isLoading) {
                SearchingIcon()
            }
        },
        supportingText = {
            Column {
                Text("输入一个要使用的simbot版本。")
                Row {
                    Text("可前往 ")
                    val link = "https://github.com/simple-robot/simpler-robot/releases"
                    ClickableText(
                        buildAnnotatedString {
                            withStyle(SpanStyle(color = Color.Blue)) {
                                append("Releases")
                                pushUrlAnnotation(UrlAnnotation(link))
                            }
                        }) {
                        window.open(link, target = "_blank")
                    }
                    Text(" 参考。")
                }
            }
        }
    )
}


@Composable
private fun GradleVersion(project: GradleProjectViewModel) {
    val isDanger = project.gradleSettings.version.isEmpty()

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Gradle版本") },
        placeholder = { Text("请输入Gradle版本") },
        supportingText = {
            Column {
                Text(
                    "输入一个要使用的Gradle版本"
                )
                AnimatedVisibility(isDanger) {
                    Text("Gradle版本不可为空！", fontWeight = FontWeight.Bold)
                }
            }
        },
        isError = isDanger,
        singleLine = true,
        value = project.gradleSettings.version,
        onValueChange = {
            project.gradleSettings.version = it
        },
    )
}

@Composable
private fun WithSpring(project: GradleProjectViewModel) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = project.withSpring,
                onCheckedChange = {
                    project.withSpring = it
                },
            )

            Text("集成 Spring Boot")
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ComponentSelection(project: GradleProjectViewModel, loadingCounter: LoadingCounter) {
    val components = project.components

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SimbotComponent.entries.forEach { simbotComponent ->
            val isSelected = components.any { it.component == simbotComponent }

            ElevatedFilterChip(
                selected = isSelected,
                onClick = {
                    if (isSelected) {
                        components.removeAll { it.component == simbotComponent }
                    } else {
                        components.add(SimbotComponentWithVersion(simbotComponent, ComponentVersion.UNKNOWN))
                    }
                },
                label = { Text(simbotComponent.display) },
                leadingIcon = {
                    AnimatedVisibility(isSelected) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                }
            )
        }
    }

    Column {
        components.forEach { componentWithVersion ->
            val component = componentWithVersion.component
            val version = componentWithVersion.version

            var loadingVersion by remember(component) { mutableStateOf(false) }

            val versionDisplay = when (version) {
                ComponentVersion.UNKNOWN -> ""
                is ComponentVersion.Value -> version.value
            }

            if (version is ComponentVersion.UNKNOWN) {
                LaunchedEffect(component) {
                    loadingVersion = true
                    loadingCounter.inc()
                    try {
                        withTimeout(5.seconds) {
                            val latest = fetchLatest(component.owner, component.repo)
                            componentWithVersion.version = ComponentVersion.Value(latest.tagName.removePrefix("v"))
                        }
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        componentWithVersion.version = ComponentVersion.Value(
                            when (component) {
                                SimbotComponent.QQ -> COMPONENT_QQ.version?.version ?: ""
                                SimbotComponent.KOOK -> COMPONENT_KOOK.version?.version ?: ""
                                SimbotComponent.OB -> COMPONENT_OB_11.version?.version ?: ""
                            }
                        )
                    } finally {
                        loadingVersion = false
                        loadingCounter.dec()
                    }
                }

                // LaunchedEffect(component) {
                //     componentWithVersion.version = ComponentVersion.Value(
                //         when (component) {
                //             SimbotComponent.QQ -> COMPONENT_QQ.version?.version ?: ""
                //             SimbotComponent.KOOK -> COMPONENT_KOOK.version?.version ?: ""
                //             SimbotComponent.OB -> COMPONENT_OB_11.version?.version ?: ""
                //         }
                //     )
                // }
            }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                enabled = !loadingVersion,
                value = versionDisplay,
                onValueChange = {
                    componentWithVersion.version = ComponentVersion.Value(it)
                },
                isError = !loadingVersion && versionDisplay.isEmpty(),
                label = {
                    Text("${component.display}版本号")
                },
                placeholder = {
                    if (loadingVersion) {
                        Text("查询中...")
                    } else {
                        Text("版本号")
                    }
                },
                trailingIcon = {
                    AnimatedVisibility(loadingVersion) {
                        SearchingIcon()
                    }
                },
            )
        }
    }
}

@Composable
private fun DoDownload(
    project: GradleProjectViewModel,
    loadingCounter: LoadingCounter
) {
    val scope = rememberCoroutineScope()

    FilledTonalButton(
        enabled = !loadingCounter.hasLoading,
        onClick = {
            loadingCounter.inc()
            scope.launch {
                kotlin.runCatching {
                    doDownload(project)
                }.onFailure { err ->
                    println("生成失败: $err")
                    window.alert("生成失败QAQ")
                }
            }.invokeOnCompletion { loadingCounter.dec() }
        }
    ) {
        Text("生成并下载")
    }
}

@Composable
private fun SearchingIcon(
    initialColor: Color = LocalContentColor.current,
    targetColor: Color = LocalContentColor.current.copy(alpha = .2f),
    animationSpec: InfiniteRepeatableSpec<Color> = infiniteRepeatable(
        tween(durationMillis = 600, delayMillis = 200),
        repeatMode = RepeatMode.Reverse
    ),
    label: String = "SearchIconColorAnimation"
) {
    val color by rememberInfiniteTransition().animateColor(
        initialValue = initialColor,
        targetValue = targetColor,
        animationSpec = animationSpec,
        label = label
    )

    Icon(
        Icons.Outlined.Search,
        "Searching",
        tint = color
    )
}

private suspend fun doDownload(
    project: GradleProjectViewModel
) {
    val name = project.projectName
    val zip = doGenerate(project)
    val blob = zip.generateAsync(
        options = JsZipFileGenerateOptions("blob"),
    ).await<Blob>()

    saveAs(blob, "$name.zip")
}
