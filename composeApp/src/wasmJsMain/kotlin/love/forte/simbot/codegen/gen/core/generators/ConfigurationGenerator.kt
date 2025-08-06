package love.forte.simbot.codegen.gen.core.generators

import jszip.JSZip
import love.forte.simbot.codegen.gen.core.CodeGenerator
import love.forte.simbot.codegen.gen.core.Component
import love.forte.simbot.codegen.gen.core.Framework
import love.forte.simbot.codegen.gen.core.GenerationContext

/**
 * 配置文件生成器。
 * 
 * 负责生成项目的配置文件，包括应用程序配置、组件配置等。
 * 
 * @author ForteScarlet
 */
interface ConfigurationGenerator : CodeGenerator {
    /**
     * 生成配置文件。
     * 
     * @param resourceDir 资源目录的 JSZip 对象
     * @param context 代码生成的上下文信息
     */
    suspend fun generateConfigurations(resourceDir: JSZip, context: GenerationContext)
    
    override suspend fun generate(zip: JSZip, context: GenerationContext) {
        val rootDir = zip.folder(context.projectName) ?: throw IllegalStateException("无法创建项目根目录: ${context.projectName}")
        val resourceDir = createResourceDirectory(rootDir)
        generateConfigurations(resourceDir, context)
    }
    
    /**
     * 创建资源目录。
     * 
     * @param rootDir 项目根目录的 JSZip 对象
     * @return 资源目录的 JSZip 对象
     */
    suspend fun createResourceDirectory(rootDir: JSZip): JSZip {
        val srcDir = rootDir.folder("src") ?: throw IllegalStateException("无法创建 src 目录")
        val mainDir = srcDir.folder("main") ?: throw IllegalStateException("无法创建 main 目录")
        return mainDir.folder("resources") ?: throw IllegalStateException("无法创建 resources 目录")
    }
}

/**
 * 框架特定的配置文件生成器。
 * 
 * 负责生成特定框架的配置文件。
 * 
 * @param F 框架类型
 */
interface FrameworkSpecificConfigurationGenerator<F : Framework> : ConfigurationGenerator {
    /**
     * 生成框架特定的配置文件。
     * 
     * @param resourceDir 资源目录的 JSZip 对象
     * @param context 代码生成的上下文信息
     */
    suspend fun generateFrameworkConfigurations(resourceDir: JSZip, context: GenerationContext)
    
    override suspend fun generateConfigurations(resourceDir: JSZip, context: GenerationContext) {
        generateFrameworkConfigurations(resourceDir, context)
        generateComponentConfigurations(resourceDir, context)
    }
    
    /**
     * 生成组件配置文件。
     * 
     * @param resourceDir 资源目录的 JSZip 对象
     * @param context 代码生成的上下文信息
     */
    suspend fun generateComponentConfigurations(resourceDir: JSZip, context: GenerationContext) {
        if (context.components.isNotEmpty()) {
            val botsDir = resourceDir.folder("simbot-bots") ?: throw IllegalStateException("无法创建 simbot-bots 目录")
            context.components.forEach { component ->
                generateComponentConfiguration(botsDir, component, context)
            }
        }
    }
    
    /**
     * 生成特定组件的配置文件。
     * 
     * @param botsDir simbot-bots 目录的 JSZip 对象
     * @param component 组件
     * @param context 代码生成的上下文信息
     */
    suspend fun generateComponentConfiguration(botsDir: JSZip, component: Component, context: GenerationContext)
}

/**
 * Spring 框架配置文件生成器。
 * 
 * 负责生成 Spring 框架的配置文件。
 */
interface SpringConfigurationGenerator : FrameworkSpecificConfigurationGenerator<Framework.Spring> {
    /**
     * 生成 Spring 应用程序配置文件。
     * 
     * @param resourceDir 资源目录的 JSZip 对象
     * @param context 代码生成的上下文信息
     */
    suspend fun generateApplicationYml(resourceDir: JSZip, context: GenerationContext)
    
    override suspend fun generateFrameworkConfigurations(resourceDir: JSZip, context: GenerationContext) {
        generateApplicationYml(resourceDir, context)
    }
}

/**
 * 核心库配置文件生成器。
 * 
 * 负责生成核心库（非 Spring）的配置文件。
 */
interface CoreConfigurationGenerator : FrameworkSpecificConfigurationGenerator<Framework.Core> {
    /**
     * 生成核心库配置文件。
     * 
     * @param resourceDir 资源目录的 JSZip 对象
     * @param context 代码生成的上下文信息
     */
    suspend fun generateCoreConfigurations(resourceDir: JSZip, context: GenerationContext)
    
    override suspend fun generateFrameworkConfigurations(resourceDir: JSZip, context: GenerationContext) {
        generateCoreConfigurations(resourceDir, context)
    }
}
