package love.forte.simbot.codegen.gen.core.generators.java

import jszip.JSZip
import love.forte.simbot.codegen.gen.core.Framework
import love.forte.simbot.codegen.gen.core.GenerationContext
import love.forte.simbot.codegen.gen.core.JavaStyle
import love.forte.simbot.codegen.gen.core.ProgrammingLanguage

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
        
        val javaCode = when (context.framework) {
            is Framework.Spring -> generateSpringMainClass(
                packageName = context.packageName,
                className = mainClassName,
                javaStyle = javaLanguage.style
            )
            is Framework.Core -> generateCoreMainClass(
                packageName = context.packageName,
                className = mainClassName,
                javaStyle = javaLanguage.style
            )
        }
        
        // 生成文件
        val fileName = JavaTemplates.getMainClassFileName(mainClassName)
        packageDir.file(fileName, javaCode)
    }
    
    /**
     * 生成 Spring Boot 主应用类代码。
     * 
     * @param packageName 包名
     * @param className 类名
     * @param javaStyle Java 编程风格（对于主类，风格不影响生成结果）
     * @return 生成的代码
     */
    private fun generateSpringMainClass(
        packageName: String,
        className: String,
        javaStyle: JavaStyle
    ): String {
        return JavaTemplates.springMainClassTemplate(packageName, className)
    }
    
    /**
     * 生成核心库主应用类代码。
     * 
     * @param packageName 包名
     * @param className 类名
     * @param javaStyle Java 编程风格
     * @return 生成的代码
     */
    private fun generateCoreMainClass(
        packageName: String,
        className: String,
        javaStyle: JavaStyle
    ): String {
        return JavaTemplates.coreMainClassTemplate(packageName, className, javaStyle)
    }
}