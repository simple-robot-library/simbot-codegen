package love.forte.simbot.codegen.gen.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import love.forte.simbot.codegen.gen.GradleProjectViewModel
import love.forte.simbot.codegen.gen.SIMBOT_VERSION
import love.forte.simbot.codegen.gen.core.JavaStyle
import love.forte.simbot.codegen.gen.core.ProgrammingLanguage
import love.forte.simbot.codegen.versions.fetchLatest
import love.forte.simbot.codegen.withLink

/**
 * 此函数用于展示一个可编辑的文本字段，用以输入或修改 Gradle 项目的名称。
 * 输入框中包含了对项目名称的要求提示，并会在项目名称为空时显示错误信息。
 *
 * @param project [GradleProjectViewModel] 类型的参数，包含了项目的基本信息，如项目名称等。
 */
@Composable
fun ProjectName(project: GradleProjectViewModel) {
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
fun ProjectPackage(project: GradleProjectViewModel) {
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
    )
}

/**
 * 语言选择组件，支持 Kotlin 和 Java
 */
@Composable
fun LanguageSelection(
    project: GradleProjectViewModel,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "编程语言",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                // Kotlin 选项
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RadioButton(
                        selected = project.programmingLanguage is ProgrammingLanguage.Kotlin,
                        onClick = {
                            project.programmingLanguage = ProgrammingLanguage.Kotlin(project.kotlinVersion)
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        "Kotlin",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Java 选项
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RadioButton(
                        selected = project.programmingLanguage is ProgrammingLanguage.Java,
                        onClick = {
                            project.programmingLanguage = ProgrammingLanguage.Java("21", project.javaStyle)
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            "Java",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        // Beta 标签
                        Surface(
                            modifier = Modifier,
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                            )
                        ) {
                            Text(
                                text = "Beta",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
                
                // Java 样式选择（仅在选择 Java 时显示）
                AnimatedVisibility(project.programmingLanguage is ProgrammingLanguage.Java) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            "Java 编程风格",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RadioButton(
                                selected = project.javaStyle == JavaStyle.BLOCKING,
                                onClick = {
                                    project.javaStyle = JavaStyle.BLOCKING
                                    // 更新 programmingLanguage 以反映新的样式
                                    if (project.programmingLanguage is ProgrammingLanguage.Java) {
                                        project.programmingLanguage = ProgrammingLanguage.Java("21", JavaStyle.BLOCKING)
                                    }
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.secondary
                                )
                            )
                            Text(
                                "阻塞式 (Blocking)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RadioButton(
                                selected = project.javaStyle == JavaStyle.ASYNC,
                                onClick = {
                                    project.javaStyle = JavaStyle.ASYNC
                                    // 更新 programmingLanguage 以反映新的样式
                                    if (project.programmingLanguage is ProgrammingLanguage.Java) {
                                        project.programmingLanguage = ProgrammingLanguage.Java("21", JavaStyle.ASYNC)
                                    }
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.secondary
                                )
                            )
                            Text(
                                "异步式 (Async)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
        
        // 版本输入组件
        when (project.programmingLanguage) {
            is ProgrammingLanguage.Kotlin -> KotlinVersion(project)
            is ProgrammingLanguage.Java -> JavaVersion(project)
        }
    }
}

/**
 * Kotlin 版本输入组件
 */
@Composable
fun KotlinVersion(project: GradleProjectViewModel) {
    EnhancedTextField(
        value = project.kotlinVersion,
        onValueChange = { 
            project.kotlinVersion = it.trim()
            // 同步更新 programmingLanguage 中的版本
            if (project.programmingLanguage is ProgrammingLanguage.Kotlin) {
                project.programmingLanguage = ProgrammingLanguage.Kotlin(it.trim())
            }
        },
        label = "Kotlin 版本",
        trailingIcon = null,
        supportingText = {
            Column {
                Text("输入一个要使用的 Kotlin 版本。请尽可能与对应的 simbot 所使用的 Kotlin 版本对应。")
            }
        }
    )
}

/**
 * Java 版本输入组件
 */
@Composable
fun JavaVersion(project: GradleProjectViewModel) {
    val javaLanguage = project.programmingLanguage as? ProgrammingLanguage.Java
    val javaVersion = javaLanguage?.version ?: "21"
    
    EnhancedTextField(
        value = javaVersion,
        onValueChange = { newVersion ->
            val trimmedVersion = newVersion.trim()
            // 更新 programmingLanguage 中的版本
            project.programmingLanguage = ProgrammingLanguage.Java(trimmedVersion, project.javaStyle)
        },
        label = "Java 版本",
        trailingIcon = null,
        supportingText = {
            Column {
                Text("输入要使用的 Java 版本。推荐使用 Java 17 或更高版本以获得更好的性能和功能支持。")
                Text("常用版本：17, 21")
            }
        }
    )
}

/**
 * Simbot 版本输入组件，支持自动获取最新版本
 */
@OptIn(ExperimentalTextApi::class)
@Composable
fun SimbotVersion(
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

/**
 * Gradle 版本输入组件
 */
@Composable
fun GradleVersion(project: GradleProjectViewModel) {
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
    )
}

/**
 * Spring Boot 集成选择组件
 */
@Composable
fun WithSpring(project: GradleProjectViewModel) {
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