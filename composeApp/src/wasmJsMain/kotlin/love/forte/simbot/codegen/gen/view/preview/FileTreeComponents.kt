package love.forte.simbot.codegen.gen.view.preview

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 计算应该自动展开的路径
 * 只展开第一级目录
 */
private fun calculateAutoExpandPaths(nodes: List<ZipFileNode>): Set<String> {
    val autoExpandPaths = mutableSetOf<String>()
    
    // 只展开根级别的目录
    for (node in nodes) {
        if (node.isDirectory) {
            autoExpandPaths.add(node.path)
        }
    }
    
    return autoExpandPaths
}

/**
 * 文件树展示组件
 * 支持展开/折叠，默认只展开第一层目录
 */
@Composable
fun FileTreeView(
    nodes: List<ZipFileNode>,
    selectedPath: String?,
    onFileSelect: (ZipFileNode) -> Unit,
    modifier: Modifier = Modifier
) {
    // 展开状态管理，默认展开第一层和自动展开单子目录
    val expandedPaths = remember(nodes) { 
        mutableStateOf(calculateAutoExpandPaths(nodes))
    }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(nodes) { node ->
            FileTreeNode(
                node = node,
                isSelected = selectedPath == node.path,
                isExpanded = expandedPaths.value.contains(node.path),
                onNodeClick = { clickedNode ->
                    if (clickedNode.isDirectory) {
                        val newExpanded = if (expandedPaths.value.contains(clickedNode.path)) {
                            expandedPaths.value - clickedNode.path
                        } else {
                            expandedPaths.value + clickedNode.path
                        }
                        expandedPaths.value = newExpanded
                    } else {
                        onFileSelect(clickedNode)
                    }
                },
                expandedPaths = expandedPaths.value
            )
        }
    }
}

/**
 * 单个文件树节点组件
 */
@Composable
private fun FileTreeNode(
    node: ZipFileNode,
    isSelected: Boolean,
    isExpanded: Boolean,
    onNodeClick: (ZipFileNode) -> Unit,
    expandedPaths: Set<String>,
    level: Int = 0
) {
    Column {
        // 节点内容
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(
                    color = when {
                        isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                        node.isDirectory -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        else -> androidx.compose.ui.graphics.Color.Transparent
                    }
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onNodeClick(node)
                }
                .padding(
                    start = (16 + level * 24).dp,
                    top = 12.dp,
                    bottom = 12.dp,
                    end = 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 展开/折叠箭头（仅目录显示）
            if (node.isDirectory) {
                Icon(
                    imageVector = if (isExpanded) {
                        Icons.Default.KeyboardArrowDown
                    } else {
                        Icons.AutoMirrored.Filled.KeyboardArrowRight
                    },
                    contentDescription = if (isExpanded) "折叠" else "展开",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.width(6.dp))
            } else {
                Spacer(modifier = Modifier.width(24.dp))
            }

            // 文件/文件夹图标
            Icon(
                imageVector = getFileIcon(node, isExpanded),
                contentDescription = if (node.isDirectory) "文件夹" else "文件",
                modifier = Modifier.size(20.dp),
                tint = getFileIconColor(node, isExpanded)
            )
            
            Spacer(modifier = Modifier.width(12.dp))

            // 文件/文件夹名称
            Text(
                text = node.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (node.isDirectory) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 15.sp,
                    lineHeight = 20.sp
                ),
                color = when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    node.isDirectory -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                }
            )

            // 文件大小信息（仅文件显示）
            if (!node.isDirectory && node.size != null) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = formatFileSize(node.size),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }

        // 子节点（仅目录且展开时显示）
        if (node.isDirectory) {
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(
                    animationSpec = androidx.compose.animation.core.spring(
                        dampingRatio = androidx.compose.animation.core.Spring.DampingRatioNoBouncy,
                        stiffness = androidx.compose.animation.core.Spring.StiffnessMedium
                    )
                ) + fadeIn(
                    animationSpec = androidx.compose.animation.core.tween(250)
                ),
                exit = shrinkVertically(
                    animationSpec = androidx.compose.animation.core.spring(
                        dampingRatio = androidx.compose.animation.core.Spring.DampingRatioNoBouncy,
                        stiffness = androidx.compose.animation.core.Spring.StiffnessMedium
                    )
                ) + fadeOut(
                    animationSpec = androidx.compose.animation.core.tween(200)
                )
            ) {
                Column(
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    node.children.forEach { childNode ->
                        FileTreeNode(
                            node = childNode,
                            isSelected = false, // 只有点击的文件才会被选中
                            isExpanded = expandedPaths.contains(childNode.path),
                            onNodeClick = onNodeClick,
                            expandedPaths = expandedPaths,
                            level = level + 1
                        )
                    }
                }
            }
        }
    }
}

/**
 * 获取文件图标
 */
@Composable
private fun getFileIcon(node: ZipFileNode, isExpanded: Boolean): ImageVector {
    return when {
        node.isDirectory -> if (isExpanded) Icons.Default.FolderOpen else Icons.Default.Folder
        else -> getFileTypeIcon(node.extension)
    }
}

/**
 * 获取文件图标颜色
 */
@Composable
private fun getFileIconColor(node: ZipFileNode, isExpanded: Boolean): androidx.compose.ui.graphics.Color {
    return when {
        node.isDirectory -> if (isExpanded) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
        } else {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        }
        else -> getFileTypeColor(node.extension)
    }
}

/**
 * 根据文件扩展名获取图标
 */
private fun getFileTypeIcon(extension: String): ImageVector {
    return when (extension.lowercase()) {
        "kt", "java", "js", "ts", "py", "cpp", "c", "h" -> Icons.Default.Description
        "xml", "json", "yml", "yaml", "properties" -> Icons.Default.Description
        "md", "txt" -> Icons.Default.Description
        "gradle" -> Icons.Default.Description
        else -> Icons.Default.Description
    }
}

/**
 * 根据文件扩展名获取图标颜色
 */
@Composable
private fun getFileTypeColor(extension: String): androidx.compose.ui.graphics.Color {
    return when (extension.lowercase()) {
        "kt" -> androidx.compose.ui.graphics.Color(0xFF8B5CF6)      // Kotlin 现代紫色
        "java" -> androidx.compose.ui.graphics.Color(0xFFFF6B35)     // Java 现代橙色
        "js", "ts" -> androidx.compose.ui.graphics.Color(0xFFF7DF1E) // JavaScript 黄色
        "xml" -> androidx.compose.ui.graphics.Color(0xFF3B82F6)      // XML 现代蓝色
        "json" -> androidx.compose.ui.graphics.Color(0xFF10B981)     // JSON 现代绿色
        "yml", "yaml" -> androidx.compose.ui.graphics.Color(0xFFEC4899) // YAML 粉色
        "properties" -> androidx.compose.ui.graphics.Color(0xFF6366F1) // Properties 靛蓝
        "md" -> androidx.compose.ui.graphics.Color(0xFF64748B)       // Markdown 石板灰
        "txt" -> androidx.compose.ui.graphics.Color(0xFF94A3B8)      // 文本文件浅灰
        "gradle" -> androidx.compose.ui.graphics.Color(0xFF059669)   // Gradle 翠绿色
        "py" -> androidx.compose.ui.graphics.Color(0xFF3776AB)       // Python 蓝色
        "cpp", "c", "h" -> androidx.compose.ui.graphics.Color(0xFF00599C) // C/C++ 深蓝
        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
    }
}

/**
 * 格式化文件大小
 */
private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "${bytes}B"
        bytes < 1024 * 1024 -> "${bytes / 1024}KB"
        else -> "${bytes / (1024 * 1024)}MB"
    }
}
