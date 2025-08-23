package love.forte.simbot.codegen.gen.view

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import js.date.Date
import love.forte.simbot.codegen.components.*
import love.forte.simbot.codegen.gen.GradleProjectViewModel
import love.forte.simbot.codegen.withLink
import kotlin.random.Random


/**
 * Composable function to display the view for configuring Gradle project settings.
 *
 * This view allows users to manage settings related to a*/
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun GradleSettingsView(
    projectViewModel: GradleProjectViewModel = viewModel { GradleProjectViewModel() },
    loadingCounter: LoadingCounter = remember { LoadingCounter() },
) {
    val windowSize = rememberWindowSize()

    val mousePositionState = rememberMousePositionState()
    val mousePosition by mousePositionState

    // 使用Box来叠放动画背景和主要内容
    Box(
        modifier = Modifier
            .fillMaxSize()
            .mousePosition(mousePositionState)
    ) {

        // 主要内容层
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
                    },
                    actions = {
                        ThemeToggleButton()
                    }
                )
            },
            content = { innerPaddings ->
                OptimizedAnimatedBackground(
                    modifier = Modifier.fillMaxSize(),
                    particleCount = remember(windowSize) {
                        when (windowSize) {
                            // 移动端使用较少粒子
                            WindowSize.Mobile -> Random.nextInt(20, 25)
                            WindowSize.Tablet -> Random.nextInt(35, 45)
                            WindowSize.Desktop -> Random.nextInt(50, 65)
                        }
                    },
                    mousePosition = mousePosition
                )

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
            ),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 页面标题
        Text(
            "项目配置",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        // 使用 GroupGrid 进行响应式布局
        GroupGrid(windowSize = windowSize) {
            // 项目信息组
            GroupCard(
                title = "项目信息",
                subtitle = "配置项目的基本信息"
            ) {
                ProjectName(project)
                ProjectPackage(project)
            }

            // 语言风格组
            GroupCard(
                title = "语言风格",
                subtitle = "选择编程语言和API风格"
            ) {
                LanguageSelectionContent(project)
            }

            // 框架集成组
            GroupCard(
                title = "框架集成",
                subtitle = "选择是否集成Spring框架"
            ) {
                WithSpringContent(project)
                SpringComponentSelector(project)
            }

            // 组件配置组
            GroupCard(
                title = "组件配置",
                subtitle = "配置simbot核心库版本和组件"
            ) {
                SimbotVersion(project, loadingCounter)
                GradleVersion(project)
                ComponentSelection(project, loadingCounter, windowSize)
            }
        }

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
                append(Date().getFullYear().toString())
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


