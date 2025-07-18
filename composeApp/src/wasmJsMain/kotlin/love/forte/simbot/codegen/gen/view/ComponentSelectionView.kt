package love.forte.simbot.codegen.gen.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.withTimeout
import love.forte.simbot.codegen.gen.*
import love.forte.simbot.codegen.versions.fetchLatest
import kotlin.time.Duration.Companion.seconds

/**
 * 组件选择视图，用于选择和配置 Simbot 组件
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ComponentSelection(
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
                }
            }
        }
    }
}