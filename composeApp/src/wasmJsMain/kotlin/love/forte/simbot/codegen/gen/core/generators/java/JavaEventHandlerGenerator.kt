package love.forte.simbot.codegen.gen.core.generators.java

import jszip.JSZip
import love.forte.simbot.codegen.gen.core.Component
import love.forte.simbot.codegen.gen.core.Framework
import love.forte.simbot.codegen.gen.core.GenerationContext
import love.forte.simbot.codegen.gen.core.JavaStyle
import love.forte.simbot.codegen.gen.core.ProgrammingLanguage

/**
 * Java 事件处理器生成器。
 * 
 * 专门负责生成 Java 事件处理器类的单一职责生成器。
 * 支持 Spring Boot 和核心库两种框架，以及阻塞和异步两种编程风格。
 * 
 * @author ForteScarlet
 */
class JavaEventHandlerGenerator {
    
    /**
     * 生成事件处理器类。
     * 
     * @param packageDir 包目录
     * @param context 生成上下文
     */
    suspend fun generateEventHandlers(packageDir: JSZip, context: GenerationContext) {
        val javaLanguage = context.language as ProgrammingLanguage.Java
        
        val handlersCode = when (context.framework) {
            is Framework.Spring -> generateSpringEventHandlers(
                packageName = context.packageName,
                components = context.components,
                javaStyle = javaLanguage.style
            )
            is Framework.Core -> generateCoreEventHandlers(
                packageName = context.packageName,
                components = context.components,
                javaStyle = javaLanguage.style
            )
        }
        
        // 创建 handle 子包目录并生成文件
        val handleDir = packageDir.folder(JavaTemplates.HANDLER_PACKAGE_SUFFIX) 
            ?: throw IllegalStateException("无法创建 ${JavaTemplates.HANDLER_PACKAGE_SUFFIX} 目录")
        val fileName = JavaTemplates.getHandlerFileName()
        handleDir.file(fileName, handlersCode)
    }
    
    /**
     * 生成 Spring 框架的事件处理器代码。
     * 
     * @param packageName 基础包名
     * @param components 组件列表
     * @param javaStyle Java 编程风格
     * @return 生成的代码
     */
    private fun generateSpringEventHandlers(
        packageName: String,
        components: List<Component>,
        javaStyle: JavaStyle
    ): String = buildString {
        // 生成类头部
        append(JavaTemplates.springEventHandlerHeaderTemplate(packageName, javaStyle == JavaStyle.ASYNC))
        appendLine()
        
        // 生成示例方法
        var showcaseNumber = 1
        
        // 1. 监听所有事件
        appendLine(JavaTemplates.allEventListenerTemplate(showcaseNumber++))
        appendLine()
        
        // 2. 过滤消息事件
        appendLine(JavaTemplates.messageEventListenerTemplate(showcaseNumber++, javaStyle))
        appendLine()
        
        // 3. 回复消息
        appendLine(JavaTemplates.replyMessageListenerTemplate(showcaseNumber++, javaStyle))
        
        // 4. 组件特定的示例（如果有组件）
        val firstComponent = components.firstOrNull()
        if (firstComponent != null) {
            appendLine()
            appendLine(JavaTemplates.componentEventListenerTemplate(showcaseNumber, firstComponent, javaStyle))
        }
        
        // 结束类
        appendLine()
        append(JavaTemplates.CLASS_CLOSE_TEMPLATE)
    }
    
    /**
     * 生成核心库框架的事件处理器代码。
     * 
     * @param packageName 基础包名
     * @param components 组件列表
     * @param javaStyle Java 编程风格
     * @return 生成的代码
     */
    private fun generateCoreEventHandlers(
        packageName: String,
        components: List<Component>,
        javaStyle: JavaStyle
    ): String = buildString {
        // 生成类头部
        append(JavaTemplates.coreEventHandlerHeaderTemplate(packageName, javaStyle == JavaStyle.ASYNC))
        appendLine()
        
        // 生成示例方法
        var showcaseNumber = 1
        
        // 1. 监听所有事件
        appendLine(JavaTemplates.allEventListenerTemplate(showcaseNumber++))
        appendLine()
        
        // 2. 过滤消息事件
        appendLine(JavaTemplates.messageEventListenerTemplate(showcaseNumber++, javaStyle))
        appendLine()
        
        // 3. 回复消息
        appendLine(JavaTemplates.replyMessageListenerTemplate(showcaseNumber++, javaStyle))
        
        // 4. 组件特定的示例（如果有组件）
        val firstComponent = components.firstOrNull()
        if (firstComponent != null) {
            appendLine()
            appendLine(JavaTemplates.componentEventListenerTemplate(showcaseNumber, firstComponent, javaStyle))
        }
        
        // 添加核心库特定注释
        appendLine()
        appendLine("    // 注意：核心库需要手动注册事件监听器到应用程序中")
        appendLine("    // 可以通过 ApplicationBuilder 的配置方法进行注册")
        
        // 结束类
        appendLine()
        append(JavaTemplates.CLASS_CLOSE_TEMPLATE)
    }
}