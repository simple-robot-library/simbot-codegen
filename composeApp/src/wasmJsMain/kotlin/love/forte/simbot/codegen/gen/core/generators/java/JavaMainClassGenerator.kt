package love.forte.simbot.codegen.gen.core.generators.java

import jszip.JSZip
import love.forte.codegentle.java.toRelativePath
import love.forte.simbot.codegen.gen.core.Framework
import love.forte.simbot.codegen.gen.core.GenerationContext
import love.forte.simbot.codegen.gen.core.JavaStyle
import love.forte.simbot.codegen.gen.core.ProgrammingLanguage
import love.forte.simbot.codegen.toRelativePath0

/**
 * Java 主应用类生成器。
 * 
 * 专门负责生成 Java 主应用类的单一职责生成器。
 * 支持 Spring Boot 和核心库两种框架，以及阻塞和异步两种编程风格。
 * 
 * @author ForteScarlet
 */
class JavaMainClassGenerator {
    
    /**
     * 生成主应用类。
     * 
     * @param packageDir 包目录
     * @param context 生成上下文
     */
    suspend fun generateMainClass(packageDir: JSZip, context: GenerationContext) {
        val javaLanguage = context.language as ProgrammingLanguage.Java
        val mainClassName = JavaTemplates.DEFAULT_MAIN_CLASS_NAME
        
        val javaFile = when (context.framework) {
            is Framework.Spring -> JavaTemplates.createSpringMainClassFile(
                packageName = context.packageName,
                className = mainClassName
            )
            is Framework.Core -> JavaTemplates.createCoreMainClassFile(
                packageName = context.packageName,
                className = mainClassName,
                javaStyle = javaLanguage.style
            )
        }
        
        // 生成文件
//        val fileName = JavaTemplates.getMainClassFileName(mainClassName)
        packageDir.file(javaFile.toRelativePath0(), javaFile.toString())
    }
    
}
