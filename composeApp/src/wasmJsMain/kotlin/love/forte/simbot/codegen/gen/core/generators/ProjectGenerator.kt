package love.forte.simbot.codegen.gen.core.generators

import jszip.JSZip
import love.forte.simbot.codegen.gen.core.CodeGenerator
import love.forte.simbot.codegen.gen.core.GenerationContext

/**
 * 项目结构生成器。
 * 
 * 负责生成项目的基本结构，包括目录结构、构建脚本等。
 * 
 * @author ForteScarlet
 */
interface ProjectGenerator : CodeGenerator {
    /**
     * 生成项目的基本结构。
     * 
     * @param rootDir 项目根目录的 JSZip 对象
     * @param context 代码生成的上下文信息
     */
    suspend fun generateProjectStructure(rootDir: JSZip, context: GenerationContext)
    
    override suspend fun generate(zip: JSZip, context: GenerationContext) {
        val rootDir = zip.folder(context.projectName) ?: throw IllegalStateException("无法创建项目根目录: ${context.projectName}")
        generateProjectStructure(rootDir, context)
    }
}

/**
 * Gradle 项目生成器。
 * 
 * 负责生成 Gradle 项目的基本结构，包括 Gradle 构建脚本、Gradle Wrapper 等。
 */
interface GradleProjectGenerator : ProjectGenerator {
    /**
     * 生成 Gradle 构建脚本。
     * 
     * @param rootDir 项目根目录的 JSZip 对象
     * @param context 代码生成的上下文信息
     */
    suspend fun generateBuildScript(rootDir: JSZip, context: GenerationContext)
    
    /**
     * 生成 Gradle 设置脚本。
     * 
     * @param rootDir 项目根目录的 JSZip 对象
     * @param context 代码生成的上下文信息
     */
    suspend fun generateSettingsScript(rootDir: JSZip, context: GenerationContext)
    
    /**
     * 生成 Gradle Wrapper。
     * 
     * @param rootDir 项目根目录的 JSZip 对象
     * @param context 代码生成的上下文信息
     */
    suspend fun generateGradleWrapper(rootDir: JSZip, context: GenerationContext)
    
    /**
     * 生成 Gradle 属性文件。
     * 
     * @param rootDir 项目根目录的 JSZip 对象
     * @param context 代码生成的上下文信息
     */
    suspend fun generateGradleProperties(rootDir: JSZip, context: GenerationContext)
    
    /**
     * 生成 Gradle 版本目录。
     * 
     * @param rootDir 项目根目录的 JSZip 对象
     * @param context 代码生成的上下文信息
     */
    suspend fun generateVersionCatalog(rootDir: JSZip, context: GenerationContext)
    
    override suspend fun generateProjectStructure(rootDir: JSZip, context: GenerationContext) {
        generateBuildScript(rootDir, context)
        generateSettingsScript(rootDir, context)
        generateGradleWrapper(rootDir, context)
        generateGradleProperties(rootDir, context)
        generateVersionCatalog(rootDir, context)
    }
}
