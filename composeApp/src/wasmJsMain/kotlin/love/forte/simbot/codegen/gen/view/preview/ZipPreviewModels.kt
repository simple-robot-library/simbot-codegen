package love.forte.simbot.codegen.gen.view.preview

import jszip.JSZip
import jszip.JSZipObject
import jszip.text

/**
 * ZIP 文件树节点数据模型
 * 表示文件树中的一个节点，可以是文件或目录
 */
data class ZipFileNode(
    /** 文件或目录名称 */
    val name: String,
    /** 完整路径 */
    val path: String,
    /** 是否为目录 */
    val isDirectory: Boolean,
    /** 子节点列表（仅目录有效） */
    val children: MutableList<ZipFileNode> = mutableListOf(),
    /** 文件大小（字节，仅文件有效） */
    val size: Long? = null,
    /** 对应的 JSZipObject，用于读取内容 */
    val zipObject: JSZipObject? = null
) {
    /** 获取文件扩展名 */
    val extension: String
        get() = if (!isDirectory && name.contains('.')) {
            name.substringAfterLast('.')
        } else ""
    
    /** 获取层级深度 */
    val depth: Int
        get() = path.count { it == '/' }
    
    /** 是否为根目录的直接子节点 */
    val isTopLevel: Boolean
        get() = depth <= 1
}

/**
 * 文件内容数据模型
 * 表示已加载的文件内容
 */
data class FileContent(
    /** 文件路径 */
    val path: String,
    /** 文件内容文本 */
    val content: String,
    /** MIME类型（根据扩展名推断） */
    val mimeType: String,
    /** 文件大小 */
    val size: Long,
    /** 是否为二进制文件 */
    val isBinary: Boolean = false
) {
    companion object {
        /** 根据文件扩展名推断MIME类型 */
        fun inferMimeType(fileName: String): String {
            return when (fileName.substringAfterLast('.').lowercase()) {
                "kt" -> "text/x-kotlin"
                "java" -> "text/x-java"
                "xml" -> "application/xml"
                "json" -> "application/json"
                "yml", "yaml" -> "text/x-yaml"
                "properties" -> "text/x-properties"
                "gradle" -> "text/x-gradle"
                "md" -> "text/markdown"
                "txt" -> "text/plain"
                "html" -> "text/html"
                "css" -> "text/css"
                "js" -> "text/javascript"
                "ts" -> "text/typescript"
                else -> "text/plain"
            }
        }
    }
}

/**
 * 预览状态枚举
 * 表示当前预览操作的状态
 */
enum class PreviewState {
    /** 空闲状态 */
    IDLE,
    /** 正在解析ZIP结构 */
    PARSING_STRUCTURE,
    /** 正在加载文件内容 */
    LOADING_CONTENT,
    /** 解析出错 */
    ERROR
}

/**
 * 文件树构建器
 * 用于从 JSZip 对象构建文件树结构
 */
class ZipFileTreeBuilder {
    
    /**
     * 从 JSZip 对象构建文件树
     * @param zip JSZip 对象
     * @return 文件树根节点列表
     */
    suspend fun buildFileTree(zip: JSZip): List<ZipFileNode> {
        val nodes = mutableMapOf<String, ZipFileNode>()
        val rootNodes = mutableListOf<ZipFileNode>()
        
        // 遍历 ZIP 中的所有文件
        zip.forEach { relativePath, zipObject ->
            val path = relativePath.trimEnd('/')
            if (path.isEmpty()) return@forEach
            
            val isDirectory = zipObject.dir
            val name = path.substringAfterLast('/')
            
            // 创建当前节点
            val currentNode = ZipFileNode(
                name = name,
                path = path,
                isDirectory = isDirectory,
                zipObject = if (!isDirectory) zipObject else null,
                size = if (!isDirectory) zipObject.unixPermissions?.toLong() else null
            )
            
            nodes[path] = currentNode
            
            // 确定父路径
            val parentPath = if (path.contains('/')) {
                path.substringBeforeLast('/')
            } else {
                null
            }
            
            if (parentPath == null) {
                // 根级节点
                rootNodes.add(currentNode)
            } else {
                // 确保父目录存在
                ensureParentDirectories(parentPath, nodes, rootNodes)
                // 将当前节点添加到父节点
                nodes[parentPath]?.children?.add(currentNode)
            }
        }
        
        // 对所有节点的子节点进行排序：目录在前，文件在后，同类型按名称排序
        sortNodes(rootNodes)
        nodes.values.forEach { node ->
            sortNodes(node.children)
        }
        
        return rootNodes
    }
    
    /**
     * 确保父目录节点存在
     */
    private fun ensureParentDirectories(
        parentPath: String,
        nodes: MutableMap<String, ZipFileNode>,
        rootNodes: MutableList<ZipFileNode>
    ) {
        if (nodes.containsKey(parentPath)) return
        
        val parentName = parentPath.substringAfterLast('/')
        val parentNode = ZipFileNode(
            name = parentName,
            path = parentPath,
            isDirectory = true
        )
        
        nodes[parentPath] = parentNode
        
        // 递归确保祖父目录存在
        val grandParentPath = if (parentPath.contains('/')) {
            parentPath.substringBeforeLast('/')
        } else {
            null
        }
        
        if (grandParentPath == null) {
            rootNodes.add(parentNode)
        } else {
            ensureParentDirectories(grandParentPath, nodes, rootNodes)
            nodes[grandParentPath]?.children?.add(parentNode)
        }
    }
    
    /**
     * 对节点列表进行排序
     * 规则：目录在前，文件在后，同类型按名称排序
     */
    private fun sortNodes(nodes: MutableList<ZipFileNode>) {
        nodes.sortWith { a, b ->
            when {
                a.isDirectory && !b.isDirectory -> -1
                !a.isDirectory && b.isDirectory -> 1
                else -> a.name.compareTo(b.name, ignoreCase = true)
            }
        }
    }
}

/**
 * 文件内容加载器
 * 负责从 JSZipObject 异步加载文件内容
 */
class FileContentLoader {
    
    /**
     * 最大可预览的文件大小（1MB）
     */
    private val maxPreviewSize = 1024 * 1024
    
    /**
     * 加载文件内容
     * @param node 文件节点
     * @return 文件内容或null（如果文件过大或为二进制文件）
     */
    suspend fun loadContent(node: ZipFileNode): FileContent? {
        if (node.isDirectory || node.zipObject == null) return null

        return try {
            val content = node.zipObject.text()
            val mimeType = FileContent.inferMimeType(node.name)
            val isBinary = false
            
            // 检查文件大小
            if (content.length > maxPreviewSize) {
                return FileContent(
                    path = node.path,
                    content = "文件过大 (${formatFileSize(content.length.toLong())})，无法预览",
                    mimeType = "text/plain",
                    size = content.length.toLong(),
                    isBinary = false
                )
            }
            
            // 检查是否为二进制文件
            if (isBinary) {
                return FileContent(
                    path = node.path,
                    content = "二进制文件，无法预览\n文件大小: ${formatFileSize(content.length.toLong())}",
                    mimeType = "text/plain",
                    size = content.length.toLong(),
                    isBinary = true
                )
            }
            
            FileContent(
                path = node.path,
                content = content,
                mimeType = mimeType,
                size = content.length.toLong(),
                isBinary = false
            )
        } catch (e: Exception) {
            FileContent(
                path = node.path,
                content = "读取文件时出错: ${e.message}",
                mimeType = "text/plain",
                size = 0,
                isBinary = false
            )
        }
    }
    
    /**
     * 判断内容是否为二进制
     */
    private fun isBinaryContent(content: String, fileName: String): Boolean {
        // 根据文件扩展名判断
        val textExtensions = setOf(
            "txt", "md", "kt", "java", "xml", "json", "yml", "yaml", 
            "properties", "gradle", "html", "css", "js", "ts", "sql"
        )
        
        val extension = fileName.substringAfterLast('.').lowercase()
        if (extension in textExtensions) return false
        
        // 检查内容中是否包含过多的非打印字符
        val nonPrintableCount = content.count { it.code < 32 && it != '\n' && it != '\r' && it != '\t' }
        return nonPrintableCount > content.length * 0.3
    }
    
    /**
     * 格式化文件大小
     */
    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            else -> "${bytes / (1024 * 1024)} MB"
        }
    }
}