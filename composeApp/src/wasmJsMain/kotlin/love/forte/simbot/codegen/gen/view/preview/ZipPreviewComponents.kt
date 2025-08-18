package love.forte.simbot.codegen.gen.view.preview

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import jszip.JSZip
import kotlinx.coroutines.launch
import love.forte.simbot.codegen.components.WindowSize

/**
 * ZIP 预览对话框
 * 主容器组件，整合文件树和文件内容预览
 */
@Composable
fun ZipPreviewDialog(
    zip: JSZip,
    windowSize: WindowSize,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        ZipPreviewContent(
            zip = zip,
            windowSize = windowSize,
            onDismiss = onDismiss,
            modifier = modifier
        )
    }
}

/**
 * ZIP 预览内容组件
 */
@Composable
private fun ZipPreviewContent(
    zip: JSZip,
    windowSize: WindowSize,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var previewState by remember { mutableStateOf(PreviewState.IDLE) }
    var fileNodes by remember { mutableStateOf<List<ZipFileNode>>(emptyList()) }
    var selectedNode by remember { mutableStateOf<ZipFileNode?>(null) }
    var fileContent by remember { mutableStateOf<FileContent?>(null) }
    var isLoadingContent by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    val treeBuilder = remember { ZipFileTreeBuilder() }
    val contentLoader = remember { FileContentLoader() }
    
    // 初始化时构建文件树
    LaunchedEffect(zip) {
        previewState = PreviewState.PARSING_STRUCTURE
        try {
            fileNodes = treeBuilder.buildFileTree(zip)
            previewState = PreviewState.IDLE
            errorMessage = null
        } catch (e: Exception) {
            previewState = PreviewState.ERROR
            errorMessage = "解析文件结构失败: ${e.message}"
        }
    }
    
    // 文件选择处理
    val onFileSelect: (ZipFileNode) -> Unit = { node ->
        selectedNode = node
        scope.launch {
            isLoadingContent = true
            previewState = PreviewState.LOADING_CONTENT
            try {
                fileContent = contentLoader.loadContent(node)
                previewState = PreviewState.IDLE
            } catch (e: Exception) {
                previewState = PreviewState.ERROR
                errorMessage = "加载文件内容失败: ${e.message}"
            } finally {
                isLoadingContent = false
            }
        }
    }
    
    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 16.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 标题栏
            PreviewHeader(
                onDismiss = onDismiss,
                previewState = previewState,
                fileCount = fileNodes.size,
                selectedFileName = selectedNode?.path
            )
            
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )
            
            // 主内容区域
            when (previewState) {
                PreviewState.PARSING_STRUCTURE -> {
                    LoadingStateContent("正在解析文件结构...")
                }
                PreviewState.ERROR -> {
                    ErrorStateContent(
                        message = errorMessage ?: "未知错误",
                        onRetry = {
                            scope.launch {
                                previewState = PreviewState.PARSING_STRUCTURE
                                try {
                                    fileNodes = treeBuilder.buildFileTree(zip)
                                    previewState = PreviewState.IDLE
                                    errorMessage = null
                                } catch (e: Exception) {
                                    previewState = PreviewState.ERROR
                                    errorMessage = "解析文件结构失败: ${e.message}"
                                }
                            }
                        }
                    )
                }
                else -> {
                    PreviewMainContent(
                        windowSize = windowSize,
                        fileNodes = fileNodes,
                        selectedNode = selectedNode,
                        fileContent = fileContent,
                        isLoadingContent = isLoadingContent,
                        onFileSelect = onFileSelect
                    )
                }
            }
        }
    }
}

/**
 * 预览标题栏
 */
@Composable
private fun PreviewHeader(
    onDismiss: () -> Unit,
    previewState: PreviewState,
    fileCount: Int,
    selectedFileName: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 标题和状态信息
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = "预览",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "项目预览",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            when (previewState) {
                PreviewState.PARSING_STRUCTURE -> {
                    Text(
                        text = "正在解析文件结构...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                PreviewState.LOADING_CONTENT -> {
                    Text(
                        text = "正在加载文件内容...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                PreviewState.ERROR -> {
                    Text(
                        text = "出现错误",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> {
                    Text(
                        text = when {
                            selectedFileName != null -> "当前文件: $selectedFileName"
                            fileCount > 0 -> "共 $fileCount 个文件和文件夹"
                            else -> "暂无文件"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // 关闭按钮
        IconButton(
            onClick = onDismiss
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "关闭",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 主内容区域
 */
@Composable
private fun PreviewMainContent(
    windowSize: WindowSize,
    fileNodes: List<ZipFileNode>,
    selectedNode: ZipFileNode?,
    fileContent: FileContent?,
    isLoadingContent: Boolean,
    onFileSelect: (ZipFileNode) -> Unit
) {
    if (windowSize == WindowSize.Mobile) {
        // 移动端：垂直布局
        MobileLayout(
            fileNodes = fileNodes,
            selectedNode = selectedNode,
            fileContent = fileContent,
            isLoadingContent = isLoadingContent,
            onFileSelect = onFileSelect
        )
    } else {
        // 桌面端和平板：水平分割布局
        DesktopLayout(
            fileNodes = fileNodes,
            selectedNode = selectedNode,
            fileContent = fileContent,
            isLoadingContent = isLoadingContent,
            onFileSelect = onFileSelect
        )
    }
}

/**
 * 移动端布局
 */
@Composable
private fun MobileLayout(
    fileNodes: List<ZipFileNode>,
    selectedNode: ZipFileNode?,
    fileContent: FileContent?,
    isLoadingContent: Boolean,
    onFileSelect: (ZipFileNode) -> Unit
) {
    var showContent by remember { mutableStateOf(false) }
    
    LaunchedEffect(selectedNode) {
        if (selectedNode != null && !selectedNode.isDirectory) {
            showContent = true
        }
    }
    
    if (showContent && selectedNode != null) {
        // 显示文件内容
        Column(modifier = Modifier.fillMaxSize()) {
            // 返回按钮
            TextButton(
                onClick = { showContent = false },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("← 返回文件列表")
            }
            
            FileContentView(
                content = fileContent,
                isLoading = isLoadingContent,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    } else {
        // 显示文件树
        FileTreeView(
            nodes = fileNodes,
            selectedPath = selectedNode?.path,
            onFileSelect = onFileSelect,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        )
    }
}

/**
 * 桌面端布局
 */
@Composable
private fun DesktopLayout(
    fileNodes: List<ZipFileNode>,
    selectedNode: ZipFileNode?,
    fileContent: FileContent?,
    isLoadingContent: Boolean,
    onFileSelect: (ZipFileNode) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        // 左侧文件树
        Card(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight()
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 标题区域
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "📁 文件结构",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                // 文件树内容区域
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(4.dp)
                ) {
                    FileTreeView(
                        nodes = fileNodes,
                        selectedPath = selectedNode?.path,
                        onFileSelect = onFileSelect,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        
        // 右侧文件内容
        FileContentView(
            content = fileContent,
            isLoading = isLoadingContent,
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
                .padding(8.dp)
        )
    }
}

/**
 * 加载状态内容
 */
@Composable
private fun LoadingStateContent(message: String) {
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
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 错误状态内容
 */
@Composable
private fun ErrorStateContent(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "❌",
                style = MaterialTheme.typography.displayMedium
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("重试")
            }
        }
    }
}