package love.forte.simbot.codegen.gen.core.generators.java

import jszip.JSZip
import love.forte.codegentle.java.InternalJavaCodeGentleApi
import love.forte.codegentle.java.toRelativePath
import love.forte.simbot.codegen.gen.core.Framework
import love.forte.simbot.codegen.gen.core.GenerationContext
import love.forte.simbot.codegen.gen.core.ProgrammingLanguage
import love.forte.simbot.codegen.toRelativePath0

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
     * @param sourceDir 包目录
     * @param context 生成上下文
     */
    @OptIn(InternalJavaCodeGentleApi::class)
    fun generateEventHandlers(sourceDir: JSZip, context: GenerationContext) {
        val javaLanguage = context.language as ProgrammingLanguage.Java
        
        val handlerFile = when (context.framework) {
            is Framework.Spring -> JavaTemplates.createSpringEventHandlerFile(
                packageName = context.packageName,
                components = context.components,
                javaStyle = javaLanguage.style
            )
            is Framework.Core -> JavaTemplates.createCoreEventHandlerFile(
                packageName = context.packageName,
                components = context.components,
                javaStyle = javaLanguage.style
            )
        }
        
        // 创建 handle 子包目录并生成文件
//        val handleDir = sourceDir.folder(JavaTemplates.HANDLER_PACKAGE_SUFFIX)
//            ?: throw IllegalStateException("无法创建 ${JavaTemplates.HANDLER_PACKAGE_SUFFIX} 目录")
//        val fileName = JavaTemplates.getHandlerFileName()
        sourceDir.file(handlerFile.toRelativePath0(), handlerFile.toString())
    }
    
}
