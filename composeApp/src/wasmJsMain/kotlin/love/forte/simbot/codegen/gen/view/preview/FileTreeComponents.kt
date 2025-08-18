package love.forte.simbot.codegen.gen.view.preview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 文件树展示组件
 * 支持展开/折叠，默认展开第一层
 */
@Composable
fun FileTreeView(
    nodes: List<ZipFileNode>,
    selectedPath: String?,
    onFileSelect: (ZipFileNode) -> Unit,
    modifier: Modifier = Modifier
) {
    // 展开状态管理，默认展开第一层
    val expandedPaths = remember { 
        mutableStateOf(
            nodes.filter { it.isDirectory && it.isTopLevel }.map { it.path }.toSet()
        ) 
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
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
                .clip(RoundedCornerShape(4.dp))
                .background(
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onNodeClick(node)
                }
                .padding(
                    start = (8 + level * 16).dp,
                    top = 4.dp,
                    bottom = 4.dp,
                    end = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 展开/折叠箭头（仅目录显示）
            if (node.isDirectory) {
                Icon(
                    imageVector = if (isExpanded) {
                        Icons.Default.KeyboardArrowDown
                    } else {
                        Icons.Default.KeyboardArrowRight
                    },
                    contentDescription = if (isExpanded) "折叠" else "展开",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
            } else {
                Spacer(modifier = Modifier.width(20.dp))
            }

            // 文件/文件夹图标
            Icon(
                imageVector = getFileIcon(node, isExpanded),
                contentDescription = if (node.isDirectory) "文件夹" else "文件",
                modifier = Modifier.size(18.dp),
                tint = getFileIconColor(node, isExpanded)
            )
            
            Spacer(modifier = Modifier.width(8.dp))

            // 文件/文件夹名称
            Text(
                text = node.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (node.isDirectory) FontWeight.Medium else FontWeight.Normal,
                    fontSize = 14.sp
                ),
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )

            // 文件大小信息（仅文件显示）
            if (!node.isDirectory && node.size != null) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = formatFileSize(node.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }

        // 子节点（仅目录且展开时显示）
        if (node.isDirectory && isExpanded) {
            AnimatedVisibility(
                visible = true,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
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
        node.isDirectory -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
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
        "kt" -> androidx.compose.ui.graphics.Color(0xFF7C4DFF)      // Kotlin 紫色
        "java" -> androidx.compose.ui.graphics.Color(0xFFED8B00)     // Java 橙色
        "xml" -> androidx.compose.ui.graphics.Color(0xFF0277BD)      // XML 蓝色
        "json" -> androidx.compose.ui.graphics.Color(0xFF2E7D32)     // JSON 绿色
        "yml", "yaml" -> androidx.compose.ui.graphics.Color(0xFF6A1B9A) // YAML 紫色
        "md" -> androidx.compose.ui.graphics.Color(0xFF424242)       // Markdown 灰色
        "gradle" -> androidx.compose.ui.graphics.Color(0xFF00796B)   // Gradle 青色
        else -> MaterialTheme.colorScheme.onSurfaceVariant
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