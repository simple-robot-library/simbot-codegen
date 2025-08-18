package love.forte.simbot.codegen.gen.view.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import simbot_codegen.composeapp.generated.resources.JetBrainsMono_Medium
import simbot_codegen.composeapp.generated.resources.Res

/**
 * 文件内容预览组件
 * 支持代码高亮和复制功能
 */
@Composable
fun FileContentView(
    content: FileContent?,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        )
    ) {
        if (isLoading) {
            LoadingContent()
        } else if (content != null) {
            FileContentDisplay(content = content)
        } else {
            EmptyContent()
        }
    }
}

/**
 * 加载状态显示
 */
@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "正在加载文件内容...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 空内容状态显示
 */
@Composable
private fun EmptyContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = "选择文件",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Text(
                text = "选择文件查看内容",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "点击左侧文件树中的文件来预览内容",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * 文件内容显示
 */
@Composable
private fun FileContentDisplay(content: FileContent) {
    Column(modifier = Modifier.fillMaxSize()) {
        // 文件信息头部
        FileHeader(content = content)

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        // 文件内容
        FileContentBody(content = content)
    }
}

/**
 * 文件信息头部
 */
@Composable
private fun FileHeader(content: FileContent) {
    val clipboardManager = LocalClipboard.current
    val scope = rememberCoroutineScope()
    var showCopyFeedback by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 文件信息
        Column {
            Text(
                text = content.path.substringAfterLast('/'),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${content.path} • ${formatFileSize(content.size)} • ${content.mimeType}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // 复制按钮
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (showCopyFeedback) {
                Text(
                    text = "已复制",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(
                onClick = {
                    scope.launch {
                        clipboardManager.nativeClipboard
                        clipboardManager.setClipEntry(ClipEntry.withPlainText(content.content))
                        showCopyFeedback = true
                        kotlinx.coroutines.delay(2000)
                        showCopyFeedback = false
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "复制内容",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * 文件内容主体
 */
@Composable
private fun FileContentBody(content: FileContent) {
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            // 行号列 - 不参与横向滚动，只跟随纵向滚动
            Box(
                modifier = Modifier
                    .verticalScroll(verticalScrollState)
            ) {
                LineNumbers(content = content.content)
            }

            // 分隔线
            VerticalDivider(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )

            // 代码内容区域 - 支持独立的横向和纵向滚动
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(horizontalScrollState)
                    .verticalScroll(verticalScrollState)
            ) {
                CodeContent(
                    content = content.content,
                    mimeType = content.mimeType
                )
            }
        }

    }
}

/**
 * 行号显示
 */
@Composable
private fun LineNumbers(content: String) {
    val jetBrainsMonoFontFamily = FontFamily(
        Font(Res.font.JetBrainsMono_Medium, FontWeight.Medium)
    )

    val lines = content.split('\n')
    val maxLineNumber = lines.size
    val lineNumberWidth = maxLineNumber.toString().length

    Column(
        modifier = Modifier.padding(end = 8.dp)
    ) {
        lines.forEachIndexed { index, _ ->
            Text(
                text = (index + 1).toString().padStart(lineNumberWidth),
                // fontFamily = jetBrainsMonoFontFamily,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = jetBrainsMonoFontFamily,
                    fontSize = 16.sp,
                    lineHeight = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(vertical = 1.dp)
            )
        }
    }
}

/**
 * 代码内容显示
 */
@Composable
private fun CodeContent(content: String, mimeType: String) {
    val jetBrainsMonoFontFamily = FontFamily(
        Font(Res.font.JetBrainsMono_Medium, FontWeight.Medium)
    )

    SelectionContainer {
        Text(
            text = content,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = jetBrainsMonoFontFamily,
                fontSize = 16.sp,
                lineHeight = 16.sp
            ),
            modifier = Modifier.padding(vertical = 1.dp),
        )
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