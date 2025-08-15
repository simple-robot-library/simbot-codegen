package love.forte.simbot.codegen.gen.view

import androidx.compose.animation.*
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import love.forte.simbot.codegen.codegen.SpringComponent
import love.forte.simbot.codegen.gen.GradleProjectViewModel
import love.forte.simbot.codegen.gen.core.ProgrammingLanguage

/**
 * SpringBoot 组件选择器
 *
 * 使用 FlowRow + FilterChip 样式，支持组件的选择和取消
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SpringComponentSelector(project: GradleProjectViewModel) {
    // 获取所有可选择的SpringComponent
    // 根据编程语言过滤组件
    val filteredAllComponents = remember(project.programmingLanguage) {
        SpringComponent.entries.filter { component ->
            when (project.programmingLanguage) {
                is ProgrammingLanguage.Kotlin -> component.forKotlin
                is ProgrammingLanguage.Java -> component.forJava
            }
        }.filter { it.selectable }
    }

    AnimatedVisibility(
        visible = project.withSpring,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
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
                    "SpringBoot 组件配置",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    "选择您需要的 SpringBoot 依赖组件",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            val selectedSpringComponentMask = project.selectedSpringComponentMask

            // 共享元素
            SharedTransitionLayout {
                Column {
                    // 组件选择区域
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        tonalElevation = 1.dp,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        FlowRow(
                            modifier = Modifier.padding(16.dp).heightIn(min = 168.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            filteredAllComponents.forEach { springComponent ->
                                val isSelected = springComponent in selectedSpringComponentMask
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

                                AnimatedVisibility(!isSelected) {
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            if (isSelected) {
                                                project.removeSelectedSpringComponent(springComponent)
                                            } else {
                                                project.addSelectedSpringComponent(springComponent)
                                            }
                                        },
                                        label = {
                                            Text(
                                                springComponent.display,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = textColor
                                            )
                                        },
                                        modifier = Modifier.sharedBounds(
                                            rememberSharedContentState(key = springComponent),
                                            animatedVisibilityScope = this,

                                            ),
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
                    }


                    // 选中组件提示
                    val selectedComponentCount = selectedSpringComponentMask.count()
                    Text(
                        text = "已选择 $selectedComponentCount 个组件",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    FlowRow(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        filteredAllComponents.forEach { springComponent ->
                            val isSelected = springComponent in selectedSpringComponentMask
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

                            AnimatedVisibility(isSelected) {
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        if (isSelected) {
                                            project.removeSelectedSpringComponent(springComponent)
                                        } else {
                                            project.addSelectedSpringComponent(springComponent)
                                        }
                                    },
                                    label = {
                                        Text(
                                            springComponent.display,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = textColor
                                        )
                                    },
                                    modifier = Modifier.sharedBounds(
                                        rememberSharedContentState(key = springComponent),
                                        animatedVisibilityScope = this
                                    ),
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Filled.Done,
                                            contentDescription = "已选择",
                                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
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
                }
            }
        }
    }
}

