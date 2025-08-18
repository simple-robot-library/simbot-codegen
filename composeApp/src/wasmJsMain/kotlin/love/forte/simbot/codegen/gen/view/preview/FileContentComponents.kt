package love.forte.simbot.codegen.gen.view.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.foundation.text.selection.SelectionContainer

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
    val clipboardManager = LocalClipboardManager.current
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
                        clipboardManager.setText(AnnotatedString(content.content))
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
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f))
    ) {
        // 内容区域
        Row(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(horizontalScrollState)
                .verticalScroll(verticalScrollState)
                .padding(8.dp)
        ) {
            // 行号列
            LineNumbers(content = content.content)
            
            // 分隔线
            VerticalDivider(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )
            
            // 代码内容
            CodeContent(
                content = content.content,
                mimeType = content.mimeType
            )
        }
    }
}

/**
 * 行号显示
 */
@Composable
private fun LineNumbers(content: String) {
    val lines = content.split('\n')
    val maxLineNumber = lines.size
    val lineNumberWidth = maxLineNumber.toString().length
    
    Column(
        modifier = Modifier.padding(end = 8.dp)
    ) {
        lines.forEachIndexed { index, _ ->
            Text(
                text = (index + 1).toString().padStart(lineNumberWidth),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
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
    val highlightedText = remember(content, mimeType) {
        highlightCode(content, mimeType)
    }
    
    SelectionContainer {
        Text(
            text = highlightedText,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                lineHeight = 16.sp
            ),
            modifier = Modifier.padding(vertical = 1.dp)
        )
    }
}

/**
 * 简单的代码高亮
 */
private fun highlightCode(content: String, mimeType: String): AnnotatedString {
    return buildAnnotatedString {
        append(content)
        
        // 根据 MIME 类型应用不同的高亮规则
        when (mimeType) {
            "text/x-kotlin" -> applyKotlinHighlight(this, content)
            "text/x-java" -> applyJavaHighlight(this, content)
            "application/xml" -> applyXmlHighlight(this, content)
            "application/json" -> applyJsonHighlight(this, content)
            else -> applyGenericHighlight(this, content)
        }
    }
}

/**
 * Kotlin 语法高亮
 */
private fun applyKotlinHighlight(builder: AnnotatedString.Builder, content: String) {
    val keywords = setOf(
        "class", "interface", "fun", "val", "var", "if", "else", "when", "for", 
        "while", "do", "try", "catch", "finally", "return", "break", "continue",
        "object", "companion", "data", "sealed", "enum", "annotation", "suspend",
        "import", "package", "private", "public", "protected", "internal"
    )
    
    highlightKeywords(builder, content, keywords, Color(0xFF0000FF)) // 蓝色关键字
    highlightStrings(builder, content, Color(0xFF008000)) // 绿色字符串
    highlightComments(builder, content, Color(0xFF808080)) // 灰色注释
}

/**
 * Java 语法高亮
 */
private fun applyJavaHighlight(builder: AnnotatedString.Builder, content: String) {
    val keywords = setOf(
        "class", "interface", "public", "private", "protected", "static", "final",
        "abstract", "synchronized", "volatile", "transient", "native", "strictfp",
        "if", "else", "switch", "case", "default", "for", "while", "do", "try",
        "catch", "finally", "throw", "throws", "return", "break", "continue",
        "import", "package", "extends", "implements", "super", "this", "new"
    )
    
    highlightKeywords(builder, content, keywords, Color(0xFF0000FF))
    highlightStrings(builder, content, Color(0xFF008000))
    highlightComments(builder, content, Color(0xFF808080))
}

/**
 * XML 语法高亮
 */
private fun applyXmlHighlight(builder: AnnotatedString.Builder, content: String) {
    // 简单的 XML 标签高亮
    val tagRegex = Regex("<[^>]+>")
    tagRegex.findAll(content).forEach { match ->
        builder.addStyle(
            style = SpanStyle(color = Color(0xFF0000FF)),
            start = match.range.first,
            end = match.range.last + 1
        )
    }
}

/**
 * JSON 语法高亮
 */
private fun applyJsonHighlight(builder: AnnotatedString.Builder, content: String) {
    // 字符串
    val stringRegex = Regex("\"[^\"]*\"")
    stringRegex.findAll(content).forEach { match ->
        builder.addStyle(
            style = SpanStyle(color = Color(0xFF008000)),
            start = match.range.first,
            end = match.range.last + 1
        )
    }
    
    // 数字
    val numberRegex = Regex("\\b\\d+(\\.\\d+)?\\b")
    numberRegex.findAll(content).forEach { match ->
        builder.addStyle(
            style = SpanStyle(color = Color(0xFFFF0000)),
            start = match.range.first,
            end = match.range.last + 1
        )
    }
}

/**
 * 通用高亮
 */
private fun applyGenericHighlight(builder: AnnotatedString.Builder, content: String) {
    // 只高亮字符串和注释
    highlightStrings(builder, content, Color(0xFF008000))
    highlightComments(builder, content, Color(0xFF808080))
}

/**
 * 关键字高亮
 */
private fun highlightKeywords(
    builder: AnnotatedString.Builder, 
    content: String, 
    keywords: Set<String>,
    color: Color
) {
    keywords.forEach { keyword ->
        val regex = Regex("\\b$keyword\\b")
        regex.findAll(content).forEach { match ->
            builder.addStyle(
                style = SpanStyle(color = color, fontWeight = FontWeight.Bold),
                start = match.range.first,
                end = match.range.last + 1
            )
        }
    }
}

/**
 * 字符串高亮
 */
private fun highlightStrings(builder: AnnotatedString.Builder, content: String, color: Color) {
    val stringRegex = Regex("\"[^\"]*\"|'[^']*'")
    stringRegex.findAll(content).forEach { match ->
        builder.addStyle(
            style = SpanStyle(color = color),
            start = match.range.first,
            end = match.range.last + 1
        )
    }
}

/**
 * 注释高亮
 */
private fun highlightComments(builder: AnnotatedString.Builder, content: String, color: Color) {
    val lineCommentRegex = Regex("//.*$", RegexOption.MULTILINE)
    lineCommentRegex.findAll(content).forEach { match ->
        builder.addStyle(
            style = SpanStyle(color = color),
            start = match.range.first,
            end = match.range.last + 1
        )
    }
    
    val blockCommentRegex = Regex("/\\*.*?\\*/", RegexOption.DOT_MATCHES_ALL)
    blockCommentRegex.findAll(content).forEach { match ->
        builder.addStyle(
            style = SpanStyle(color = color),
            start = match.range.first,
            end = match.range.last + 1
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