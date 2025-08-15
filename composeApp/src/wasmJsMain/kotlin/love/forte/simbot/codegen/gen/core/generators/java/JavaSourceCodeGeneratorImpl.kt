package love.forte.simbot.codegen.gen.core.generators.java

import jszip.JSZip
import love.forte.simbot.codegen.gen.core.GenerationContext
import love.forte.simbot.codegen.gen.core.generators.JavaSourceCodeGenerator

/**
 * Java 源代码生成器实现。
 *
 * 使用模块化组件协调 Java 源代码文件的生成。
 * 负责协调主应用类和事件处理器的生成工作。
 * 
 * 此实现采用了组合模式，将具体的生成逻辑委托给专门的生成器组件：
 * - [JavaMainClassGenerator]: 负责主应用类生成
 * - [JavaEventHandlerGenerator]: 负责事件处理器生成
 *
 * @author ForteScarlet
 */
class JavaSourceCodeGeneratorImpl : JavaSourceCodeGenerator {
    
    private val mainClassGenerator = JavaMainClassGenerator()
    private val eventHandlerGenerator = JavaEventHandlerGenerator()
    
    override suspend fun generateApplicationEntry(sourceDir: JSZip, context: GenerationContext) {
        mainClassGenerator.generateMainClass(sourceDir, context)
    }
    
    override suspend fun generateEventHandlers(sourceDir: JSZip, context: GenerationContext) {
        eventHandlerGenerator.generateEventHandlers(sourceDir, context)
    }
    
}
