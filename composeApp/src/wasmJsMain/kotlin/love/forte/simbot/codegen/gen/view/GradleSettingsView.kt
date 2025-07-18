package love.forte.simbot.codegen.gen.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import love.forte.simbot.codegen.JsDate
import love.forte.simbot.codegen.filesaver.saveAs
import love.forte.simbot.codegen.gen.*
import love.forte.simbot.codegen.jszip.JsZipFileGenerateOptions
import love.forte.simbot.codegen.versions.fetchLatest
import love.forte.simbot.codegen.withLink
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradleSettingsView(
    projectViewModel: GradleProjectViewModel = viewModel { GradleProjectViewModel() },
    loadingCounter: LoadingCounter = remember { LoadingCounter() },
) {
    val windowSize = rememberWindowSize()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                title = {
                    Text(
                        "Simbot Codegen",
                        fontWeight = FontWeight.Bold,
                        style = when (windowSize) {
                            WindowSize.Mobile -> MaterialTheme.typography.headlineSmall
                            else -> MaterialTheme.typography.headlineMedium
                        }
                    )
                }
            )
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
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        focusManager.clearFocus()
                    },
                contentAlignment = Alignment.TopCenter,
            ) {
                SettingViewContent(projectViewModel, loadingCounter)
            }
        }
    )
}


@Composable
private fun SettingViewContent(
    project: GradleProjectViewModel,
    loadingCounter: LoadingCounter,
) {
    val windowSize = rememberWindowSize()

    Box(
        modifier = Modifier.fillMaxWidth(
            when (windowSize) {
                WindowSize.Mobile -> 0.95f
                WindowSize.Tablet -> 0.75f
                WindowSize.Desktop -> 0.45f
            }
        )
    ) {
        SettingsForm(project, loadingCounter, windowSize)
    }
}

@Composable
private fun SettingsForm(
    project: GradleProjectViewModel,
    loadingCounter: LoadingCounter,
    windowSize: WindowSize
) {
    Column(
        modifier = Modifier
            .focusGroup()
            .padding(
                // 移动端使用更小的内边距
                when (windowSize) {
                    WindowSize.Mobile -> 8.dp
                    else -> 16.dp
                }

            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(
                // 移动端使用更小的内边距
                when (windowSize) {
                    WindowSize.Mobile -> 16.dp
                    else -> 24.dp
                }

            ),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(
            // 移动端使用更小的间距
            when (windowSize) {
                WindowSize.Mobile -> 12.dp
                else -> 20.dp
            },
            Alignment.Top

        ),
    )
    {
        Text(
            "项目配置",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        ProjectName(project)
        ProjectPackage(project)
        LanguageSelection(project)
        SimbotVersion(project, loadingCounter)
        GradleVersion(project)
        WithSpring(project)
        ComponentSelection(project, loadingCounter, windowSize)

        Spacer(modifier = Modifier.height(8.dp))

        // Do download
        DoDownload(project, loadingCounter, windowSize)

        Spacer(modifier = Modifier.height(24.dp))

        // Footer at the bottom of the content
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val textWithLink = buildAnnotatedString {
                append("© ")
                append(JsDate().getFullYear().toString())
                append(" ")
                withLink(text = "Simple Robot", url = "https://github.com/simple-robot")
                append(" All rights reserved.")
            }

            Text(
                text = textWithLink,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
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
    val isDanger = project.projectName.isEmpty()

    EnhancedTextField(
        value = project.projectName,
        onValueChange = { project.projectName = it },
        label = "项目名称",
        placeholder = "你的项目名称",
        isError = isDanger,
        singleLine = true,
        supportingText = {
            Column {
                Text("你的项目名称。同时也会作为项目的工件名。")
                Text("建议仅包含大小写英文、数字和下划线，不以数字为开头，且长度不可大于60。")
                AnimatedVisibility(isDanger) {
                    Text(
                        "项目名称不可为空！",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
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

    EnhancedTextField(
        value = project.projectPackage,
        onValueChange = { project.projectPackage = it },
        label = "项目包",
        placeholder = "你的项目根包结构",
        isError = isDanger,
        singleLine = true,
        supportingText = {
            Column {
                Text(
                    "你的项目根包结构。建议符合正则 [a-zA-Z][a-zA-Z0-9_]*(\\.[a-zA-Z][a-zA-Z0-9_]*)* ，" +
                            "包结构应当至少有一层，整体长度不可大于120。"
                )
                AnimatedVisibility(isDanger) {
                    Text(
                        "项目包结构格式不匹配！",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        // isError = isDanger,
        // singleLine = true,
        // value = project.projectPackage,
        // onValueChange = {
        //     project.projectPackage = it
        // },
    )
}

@Composable
private fun LanguageSelection(
    project: GradleProjectViewModel,
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Row(
            //     modifier = Modifier.padding(horizontal = 6.dp),
            //     verticalAlignment = Alignment.CenterVertically,
            // ) {
            RadioButton(
                selected = true,
                enabled = false,
                onClick = {},
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary
                )
            )

            Text(
                "Kotlin",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            // }
        }
    }
    KotlinVersion(project)
}

@Composable
private fun KotlinVersion(project: GradleProjectViewModel) {
    EnhancedTextField(
        value = project.kotlinVersion,
        onValueChange = { project.kotlinVersion = it.trim() },
        label = "Kotlin 版本",
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

    EnhancedTextField(
        value = simbotVersion ?: "",
        onValueChange = { project.simbotVersion = it.trim() },
        label = "simbot版本",
        enabled = !isLoading,
        trailingIcon = {
            AnimatedVisibility(isLoading) {
                SearchingIcon()
            }
        },
        supportingText = {
            Column {
                Text("输入一个要使用的simbot版本。")
                Row {
                    Text(
                        buildAnnotatedString {
                            append("可前往 ")
                            withLink("Releases", "https://github.com/simple-robot/simpler-robot/releases")
                            append(" 参考并选择版本。")
                        }
                    )
                }
            }
        }
    )
}


@Composable
private fun GradleVersion(project: GradleProjectViewModel) {
    val isDanger = project.gradleSettings.version.isEmpty()

    EnhancedTextField(
        value = project.gradleSettings.version,
        onValueChange = { project.gradleSettings.version = it },
        label = "Gradle版本",
        placeholder = "请输入Gradle版本",
        isError = isDanger,
        singleLine = true,
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
        // isError = isDanger,
        // singleLine = true,
        // value = project.gradleSettings.version,
        // onValueChange = {
        //     project.gradleSettings.version = it
        // },
    )
}

@Composable
private fun WithSpring(project: GradleProjectViewModel) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .hoverable(interactionSource),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isHovered)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (project.withSpring)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Checkbox(
                checked = project.withSpring,
                onCheckedChange = {
                    project.withSpring = it
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.outline
                )
            )

            Text(
                "集成 Spring Boot",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (project.withSpring) FontWeight.Medium else FontWeight.Normal
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ComponentSelection(
    project: GradleProjectViewModel,
    loadingCounter: LoadingCounter,
    windowSize: WindowSize
) {
    val components = project.components

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ) {
        // 标题区域
        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            Text(
                "组件选择",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Text(
                "选择您需要的组件，系统将自动获取最新版本",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // 组件选择区域
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            tonalElevation = 1.dp,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            FlowRow(
                modifier = Modifier.padding(
                    // 移动设备使用更小的内边距
                    when (windowSize) {
                        WindowSize.Mobile -> 8.dp
                        else -> 16.dp
                    }
                ),
                horizontalArrangement = Arrangement.spacedBy(
                    when (windowSize) {
                        WindowSize.Mobile -> 8.dp
                        else -> 12.dp
                    }
                ),
                verticalArrangement = Arrangement.spacedBy(
                    when (windowSize) {
                        WindowSize.Mobile -> 8.dp
                        else -> 12.dp
                    }
                ),
            ) {
                SimbotComponent.entries.forEach { simbotComponent ->
                    val isSelected = components.any { it.component == simbotComponent }
                    val interactionSource = remember { MutableInteractionSource() }
                    val isHovered by interactionSource.collectIsHoveredAsState()

                    val backgroundColor by animateColorAsState(
                        targetValue = when {
                            isSelected -> MaterialTheme.colorScheme.primaryContainer
                            isHovered -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                            else -> MaterialTheme.colorScheme.surface
                        },
                        label = "背景颜色动画"
                    )

                    val borderColor by animateColorAsState(
                        targetValue = when {
                            isSelected -> MaterialTheme.colorScheme.primary
                            isHovered -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            else -> MaterialTheme.colorScheme.outlineVariant
                        },
                        label = "边框颜色动画"
                    )

                    val textColor by animateColorAsState(
                        targetValue = when {
                            isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                        label = "文本颜色动画"
                    )

                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (isSelected) {
                                components.removeAll { it.component == simbotComponent }
                            } else {
                                components.add(SimbotComponentWithVersion(simbotComponent, ComponentVersion.UNKNOWN))
                            }
                        },
                        label = {
                            Text(
                                simbotComponent.display,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = textColor
                            )
                        },
                        leadingIcon = {
                            AnimatedVisibility(isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = "已选择",
                                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        interactionSource = interactionSource,
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = backgroundColor,
                            labelColor = textColor,
                            selectedContainerColor = backgroundColor,
                            selectedLabelColor = textColor,
                            iconColor = MaterialTheme.colorScheme.primary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = borderColor,
                            selectedBorderColor = borderColor,
                            selectedBorderWidth = 1.5.dp,
                            borderWidth = 1.dp
                        ),
                        elevation = FilterChipDefaults.filterChipElevation(
                            elevation = 0.dp,
                            pressedElevation = 2.dp,
                            hoveredElevation = if (isSelected) 0.dp else 1.dp
                        )
                    )
                }
            }
        }

        // 所选组件的版本配置区域
        AnimatedVisibility(visible = components.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                if (components.isNotEmpty()) {
                    Text(
                        "组件版本配置",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

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
                                    componentWithVersion.version =
                                        ComponentVersion.Value(latest.tagName.removePrefix("v"))
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
                    }

                    EnhancedTextField(
                        value = versionDisplay,
                        onValueChange = {
                            componentWithVersion.version = ComponentVersion.Value(it)
                        },
                        enabled = !loadingVersion,
                        isError = !loadingVersion && versionDisplay.isEmpty(),
                        label = "${component.display}版本号",
                        placeholder = if (loadingVersion) "查询中..." else "版本号",
                        trailingIcon = {
                            AnimatedVisibility(loadingVersion) {
                                SearchingIcon()
                            }
                        },
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // OutlinedTextField(
                    //     modifier = Modifier
                    //         .fillMaxWidth()
                    //         .padding(bottom = 12.dp),
                    //     enabled = !loadingVersion,
                    //     value = versionDisplay,
                    //     onValueChange = {
                    //         componentWithVersion.version = ComponentVersion.Value(it)
                    //     },
                    //     isError = !loadingVersion && versionDisplay.isEmpty(),
                    //     label = {
                    //         Text("${component.display}版本号")
                    //     },
                    //     placeholder = {
                    //         if (loadingVersion) {
                    //             Text("查询中...")
                    //         } else {
                    //             Text("版本号")
                    //         }
                    //     },
                    //     trailingIcon = {
                    //         AnimatedVisibility(loadingVersion) {
                    //             SearchingIcon()
                    //         }
                    //     },
                    //     shape = RoundedCornerShape(8.dp)
                    // )
                }
            }
        }
    }
}

@Composable
private fun DoDownload(
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
                    println("生成失败: $err")
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

    //
    // FilledTonalButton(
    //     enabled = !loadingCounter.hasLoading,
    //     onClick = {
    //         loadingCounter.inc()
    //         scope.launch {
    //             kotlin.runCatching {
    //                 doDownload(project)
    //             }.onFailure { err ->
    //                 println("生成失败: $err")
    //                 window.alert("生成失败QAQ")
    //             }
    //         }.invokeOnCompletion { loadingCounter.dec() }
    //     }
    // ) {
    //     Text("生成并下载")
    // }
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
    val infiniteTransition = rememberInfiniteTransition(label = "SearchIconTransition")
    val color by infiniteTransition.animateColor(
        initialValue = initialColor,
        targetValue = targetColor,
        animationSpec = animationSpec,
        label = label
    )
    val size by infiniteTransition.animateFloat(
        initialValue = 24f,
        targetValue = 28f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 800, delayMillis = 100),
            repeatMode = RepeatMode.Reverse
        ),
        label = "IconSizeAnimation"
    )


    Icon(
        Icons.Outlined.Search,
        "Searching",
        tint = color,
        modifier = Modifier.size(size.dp)
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

/**
 * 定制化的输入框组件，提供统一的样式和交互体验
 */
@Composable
private fun EnhancedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    placeholder: String? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    singleLine: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    val windowSize = rememberWindowSize()
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderColor by animateColorAsState(
        targetValue = when {
            isError -> MaterialTheme.colorScheme.error
            isFocused -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
        },
        animationSpec = tween(durationMillis = 200),
        label = "边框颜色动画"
    )

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isFocused -> MaterialTheme.colorScheme.surfaceVariant // .copy(alpha = 0.3f)
            else -> MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(durationMillis = 200),
        label = "背景颜色动画"
    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        label = {
            Text(
                text = label,
                fontWeight = if (isFocused) FontWeight.Medium else FontWeight.Normal,
                // 在移动设备上使用更小的字体
                fontSize = when (windowSize) {
                    WindowSize.Mobile -> 14.sp
                    else -> 16.sp
                }
            )
        },
        placeholder = placeholder?.let { { Text(it) } },
        supportingText = supportingText,
        isError = isError,
        singleLine = singleLine,
        enabled = enabled,
        interactionSource = interactionSource,
        trailingIcon = trailingIcon,
        leadingIcon = leadingIcon,
        shape = RoundedCornerShape(
            // 在移动设备上使用更小的圆角
            when (windowSize) {
                WindowSize.Mobile -> 8.dp
                else -> 12.dp
            }
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = borderColor,
            unfocusedBorderColor = borderColor.copy(alpha = 0.7f),
            focusedContainerColor = backgroundColor,
            unfocusedContainerColor = backgroundColor,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            errorBorderColor = MaterialTheme.colorScheme.error,
            errorContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f),
            errorLabelColor = MaterialTheme.colorScheme.error,
            errorCursorColor = MaterialTheme.colorScheme.error,
            errorSupportingTextColor = MaterialTheme.colorScheme.error,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        ),
    )
}

@Composable
fun rememberWindowSize(): WindowSize {
    var windowSize by remember { mutableStateOf(WindowSize.Desktop) }

    DisposableEffect(Unit) {
        val resizeListener: (JsAny?) -> Unit = {
            val width = window.innerWidth
            windowSize = when {
                width < 600 -> WindowSize.Mobile
                width < 840 -> WindowSize.Tablet
                else -> WindowSize.Desktop
            }
        }

        // 初始化窗口大小
        resizeListener(null)

        // 添加窗口调整大小的监听器
        window.addEventListener("resize", resizeListener)

        // 清理监听器
        onDispose {
            window.removeEventListener("resize", resizeListener)
        }
    }

    return windowSize
}

enum class WindowSize {
    Mobile, Tablet, Desktop
}