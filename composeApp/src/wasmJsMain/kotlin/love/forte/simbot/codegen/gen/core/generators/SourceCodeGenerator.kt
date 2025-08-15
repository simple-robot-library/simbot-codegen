package love.forte.simbot.codegen.gen.core.generators

import jszip.JSZip
import love.forte.simbot.codegen.gen.core.CodeGenerator
import love.forte.simbot.codegen.gen.core.GenerationContext
import love.forte.simbot.codegen.gen.core.ProgrammingLanguage

/**
 * 源代码生成器。
 * 
 * 负责生成项目的源代码文件，包括应用程序入口、事件处理器等。
 * 
 * @author ForteScarlet
 */
interface SourceCodeGenerator : CodeGenerator {
    /**
     * 生成源代码文件。
     * 
     * @param sourceDir 源代码目录的 JSZip 对象
     * @param context 代码生成的上下文信息
     */
    suspend fun generateSourceCode(sourceDir: JSZip, context: GenerationContext)
    
    override suspend fun generate(zip: JSZip, context: GenerationContext) {
        val rootDir = zip.folder(context.projectName) ?: throw IllegalStateException("无法创建项目根目录: ${context.projectName}")
        val sourceDir = createSourceDirectory(rootDir, context)
        generateSourceCode(sourceDir, context)
    }
    
    /**
     * 创建源代码目录。
     * 
     * 根据不同的项目结构和语言，创建相应的源代码目录。
     * 
     * @param rootDir 项目根目录的 JSZip 对象
     * @param context 代码生成的上下文信息
     * @return 源代码目录的 JSZip 对象
     */
    suspend fun createSourceDirectory(rootDir: JSZip, context: GenerationContext): JSZip {
        val srcDir = rootDir.folder("src") ?: throw IllegalStateException("无法创建 src 目录")
        val mainDir = srcDir.folder("main") ?: throw IllegalStateException("无法创建 main 目录")
        
        // 根据语言创建相应的源代码目录
        return when (context.language) {
            is ProgrammingLanguage.Kotlin -> {
                val kotlinDir = mainDir.folder("kotlin") ?: throw IllegalStateException("无法创建 kotlin 目录")
//                createPackageDirectories(kotlinDir, context.packageName)
                kotlinDir
            }
            is ProgrammingLanguage.Java -> {
                val javaDir = mainDir.folder("java") ?: throw IllegalStateException("无法创建 java 目录")
//                createPackageDirectories(javaDir, context.packageName)
                javaDir
            }
        }
    }
    
    /**
     * 创建包目录。
     * 
     * 根据包名创建相应的目录结构。
     * 
     * @param sourceDir 源代码目录的 JSZip 对象
     * @param packageName 包名
     * @return 包目录的 JSZip 对象
     */
    suspend fun createPackageDirectories(sourceDir: JSZip, packageName: String): JSZip {
        var currentDir = sourceDir
        packageName.split('.').forEach { pkg ->
            currentDir = currentDir.folder(pkg) ?: throw IllegalStateException("无法创建包目录: $pkg")
        }
        return currentDir
    }
}

/**
 * 语言特定的源代码生成器。
 * 
 * 负责生成特定语言的源代码文件。
 * 
 * @param L 编程语言类型
 */
interface LanguageSpecificSourceCodeGenerator<L : ProgrammingLanguage> : SourceCodeGenerator {
    /**
     * 生成应用程序入口文件。
     * 
     * @param sourceDir 包目录的 JSZip 对象
     * @param context 代码生成的上下文信息
     */
    suspend fun generateApplicationEntry(sourceDir: JSZip, context: GenerationContext)
    
    /**
     * 生成事件处理器文件。
     * 
     * @param sourceDir 包目录的 JSZip 对象
     * @param context 代码生成的上下文信息
     */
    suspend fun generateEventHandlers(sourceDir: JSZip, context: GenerationContext)
    
    override suspend fun generateSourceCode(sourceDir: JSZip, context: GenerationContext) {
        // sourceDir already points to the correct package directory from createSourceDirectory
        generateApplicationEntry(sourceDir, context)
        generateEventHandlers(sourceDir, context)
    }
}

/**
 * Kotlin 源代码生成器。
 * 
 * 负责生成 Kotlin 语言的源代码文件。
 */
interface KotlinSourceCodeGenerator : LanguageSpecificSourceCodeGenerator<ProgrammingLanguage.Kotlin>

/**
 * Java 源代码生成器。
 * 
 * 负责生成 Java 语言的源代码文件。
 */
interface JavaSourceCodeGenerator : LanguageSpecificSourceCodeGenerator<ProgrammingLanguage.Java>
