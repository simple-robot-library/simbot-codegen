package love.forte.simbot.codegen.gen.view.preview

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.snipme.highlights.Highlights
import dev.snipme.highlights.model.BoldHighlight
import dev.snipme.highlights.model.ColorHighlight
import dev.snipme.highlights.model.SyntaxLanguage
import dev.snipme.highlights.model.SyntaxTheme
import kotlinx.coroutines.launch
import love.forte.simbot.codegen.ColorMode
import love.forte.simbot.codegen.LocalAppContext
import org.jetbrains.compose.resources.Font
import simbot_codegen.composeapp.generated.resources.*

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
    val jetBrainsMonoFontFamily = FontFamily(
        Font(Res.font.JetBrainsMono_Medium, FontWeight.Medium),
        Font(Res.font.JetBrainsMono_Bold, FontWeight.Bold),
        Font(Res.font.JetBrainsMono_Thin, FontWeight.Thin),
        Font(Res.font.JetBrainsMono_Light, FontWeight.Light),
        Font(Res.font.JetBrainsMono_ExtraBold, FontWeight.ExtraBold),
        Font(Res.font.JetBrainsMono_ExtraLight, FontWeight.ExtraLight),
    )

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
                LineNumbers(content = content.content, fontFamily = jetBrainsMonoFontFamily)
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
                    fontFamily = jetBrainsMonoFontFamily,
                    mimeType = content.mimeType
                )
            }
        }

        // 垂直滚动条
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(verticalScrollState)
        )

        // 水平滚动条
        HorizontalScrollbar(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            adapter = rememberScrollbarAdapter(horizontalScrollState)
        )
    }
}

/**
 * 行号显示
 */
@Composable
private fun LineNumbers(content: String, fontFamily: FontFamily) {
    val lines = content.split('\n')
    val maxLineNumber = lines.size
    val lineNumberWidth = maxLineNumber.toString().length

    val textStyle = MaterialTheme.typography.bodySmall.copy(
        fontFamily = fontFamily,
        fontSize = 16.sp,
        lineHeight = 24.sp // 增加行高以改善对齐
    )

    Column(
        modifier = Modifier.padding(end = 8.dp)
    ) {
        lines.forEachIndexed { index, _ ->
            Box(
                modifier = Modifier.height(24.dp), // 固定高度确保对齐
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = (index + 1).toString().padStart(lineNumberWidth),
                    style = textStyle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                )
            }
        }
    }
}

// 暗色主题 - 柔和护眼的配色方案
private val darkSyntaxTheme = SyntaxTheme(
    key = "custom-dark",    // 主题名称
    code = 0xABB2BF,       // 浅灰色 - 默认代码颜色
    keyword = 0xC678DD,     // 柔和紫色 - 关键字（if, for, class等）
    string = 0x98C379,      // 柔和绿色 - 字符串
    literal = 0xD19A66,     // 暖橙色 - 数字和字面量
    comment = 0x5C6370,     // 中灰色 - 注释
    metadata = 0x61AFEF,    // 柔和蓝色 - 元数据和注解
    multilineComment = 0x5C6370, // 中灰色 - 多行注释
    punctuation = 0xABB2BF, // 浅灰色 - 标点符号
    mark = 0xE5C07B         // 金黄色 - 高亮标记
)

// 亮色主题 - 清晰明快的配色方案
private val lightSyntaxTheme = SyntaxTheme(
    key = "custom-light",   // 主题名称
    code = 0x24292E,       // 深灰色 - 默认代码颜色
    keyword = 0x6F42C1,     // 深紫色 - 关键字（if, for, class等）
    string = 0x032F62,      // 深蓝色 - 字符串
    literal = 0xE36209,     // 橙色 - 数字和字面量
    comment = 0x6A737D,     // 灰色 - 注释
    metadata = 0x005CC5,    // 蓝色 - 元数据和注解
    multilineComment = 0x6A737D, // 灰色 - 多行注释
    punctuation = 0x24292E, // 深灰色 - 标点符号
    mark = 0xB08800         // 深金色 - 高亮标记
)

/**
 * 获取自定义的语法高亮主题
 * 根据明暗模式返回适合的主题，符合Material 3设计规范
 *
 * 设计原则：
 * - 亮色主题：使用高对比度、清晰明快的颜色，与Material 3亮色调和谐
 * - 暗色主题：使用柔和、护眼的颜色，与Material 3暗色调协调
 */
private fun getCustomSyntaxTheme(darkMode: Boolean) = if (darkMode) {
    darkSyntaxTheme
} else {
    lightSyntaxTheme
}

/**
 * 代码内容显示
 *
 * @param mimeType see [FileContent.inferMimeType]
 */
@Composable
private fun CodeContent(content: String, fontFamily: FontFamily, mimeType: String? = null) {
    val appContext = LocalAppContext.current

    val textStyle = MaterialTheme.typography.bodySmall.copy(
        fontFamily = fontFamily,
        fontSize = 16.sp,
        lineHeight = 24.sp // 与行号保持一致的行高
    )

    val lang: SyntaxLanguage = remember(mimeType) {
        when {
            mimeType == null -> SyntaxLanguage.DEFAULT
            mimeType.endsWith("java", true) -> SyntaxLanguage.JAVA
            mimeType.endsWith("js", true) -> SyntaxLanguage.JAVASCRIPT
            mimeType.endsWith("kt", true) -> SyntaxLanguage.KOTLIN
            mimeType.endsWith("kts", true) -> SyntaxLanguage.KOTLIN
            mimeType.endsWith("sh", true) -> SyntaxLanguage.SHELL
            else -> SyntaxLanguage.DEFAULT
        }
    }

    val contentStringLines: MutableList<AnnotatedString> = remember(content) {
        buildList {
            AnnotatedString(content).splitLines { add(it) }
        }.toMutableStateList()
    }

    LaunchedEffect(content, appContext.colorMode) {
        val contentCodeWithReplacedLineChar = content.replace(Regex("\r\n|\r|\n"), "\n")
        val highlights = Highlights.Builder()
            .language(lang)
            .code(contentCodeWithReplacedLineChar)
            .theme(getCustomSyntaxTheme(appContext.colorMode == ColorMode.DARK))
            .build()

        val contentString = buildAnnotatedString {
            append(contentCodeWithReplacedLineChar)
            for (highlight in highlights.getHighlights()) {
                val location = highlight.location
                when (highlight) {
                    is ColorHighlight -> {
                        val rgb: Int = highlight.rgb
                        val color = Color(
                            red = rgb shr 16 and 0xFF,
                            green = rgb shr 8 and 0xFF,
                            blue = rgb and 0xFF,
                        )
                        // TODO 会有一些超出去的
                        //  add highlight style: PhraseLocation(start=3023, end=2851)
                        //  add highlight style: PhraseLocation(start=6205, end=3179)
                        if (location.start <= location.end) {
                            addStyle(SpanStyle(color = color), location.start, location.end)
                        }
                    }

                    is BoldHighlight -> {
                        if (location.start <= location.end) {
                            addStyle(SpanStyle(fontWeight = FontWeight.Bold), location.start, location.end)
                        }
                    }
                }
            }
        }

        var i = 0
        contentString.splitLines {
            contentStringLines[i] = it
            i++
        }
    }

    SelectionContainer {
        Column {
            @Composable
            fun lineContent(line: AnnotatedString) {
                Box(
                    modifier = Modifier.height(24.dp), // 固定高度确保对齐
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row {
                        Text(
                            text = line.ifEmpty { AnnotatedString(" ") }, // 空行显示空格以保持高度
                            style = textStyle,
                            maxLines = 1,
                        )
                        Text(text = "\n", maxLines = 1)
                    }
                }
            }

            for (line in contentStringLines) {
                lineContent(line)
            }
        }
    }
}

private inline fun AnnotatedString.splitLines(onLine: (AnnotatedString) -> Unit) {
    val contentString = this
    var nextStart = 0
    var nextEnd = contentString.indexOf('\n')
    while (nextEnd >= 0) {
        val line = contentString.subSequence(nextStart, nextEnd)
        onLine(line)
        nextStart = nextEnd + 1
        nextEnd = contentString.indexOf('\n', nextStart)
    }

    if (nextStart < contentString.length) {
        val line = contentString.subSequence(nextStart, contentString.length)
        onLine(line)
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
