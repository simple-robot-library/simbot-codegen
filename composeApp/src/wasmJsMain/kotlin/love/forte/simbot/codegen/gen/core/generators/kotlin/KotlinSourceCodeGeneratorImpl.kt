package love.forte.simbot.codegen.gen.core.generators.kotlin

import jszip.JSZip
import love.forte.simbot.codegen.codegen.SimbotComponent
import love.forte.simbot.codegen.gen.core.Framework
import love.forte.simbot.codegen.gen.core.GenerationContext
import love.forte.simbot.codegen.gen.core.context.ComponentImpl
import love.forte.simbot.codegen.gen.core.generators.KotlinSourceCodeGenerator
import love.forte.simbot.codegen.gen.emitSpringListenerShowcases
import love.forte.simbot.codegen.gen.emitSpringMainFile

/**
 * Kotlin源代码生成器实现。
 *
 * 使用现有的Kotlin代码生成逻辑来生成源代码。
 *
 * @author ForteScarlet
 */
class KotlinSourceCodeGeneratorImpl : KotlinSourceCodeGenerator {
    /**
     * 生成应用程序入口文件。
     *
     * @param sourceDir 包目录的JSZip对象
     * @param context 代码生成的上下文信息
     */
    override suspend fun generateApplicationEntry(sourceDir: JSZip, context: GenerationContext) {
        when (context.framework) {
            is Framework.Spring -> {
                // sourceDir 已经指向 src/main/kotlin，直接调用 emitSpringMainFile
                // emitSpringMainFile 内部会通过 toRelativePath() 创建正确的包目录结构
                emitSpringMainFile(context.packageName, sourceDir)
            }

            is Framework.Core -> {
                // TODO: 实现Core框架的主类生成
                // 暂时使用空实现，保持向后兼容
            }
        }
    }

    /**
     * 生成事件处理器文件。
     *
     * @param sourceDir 包目录的JSZip对象
     * @param context 代码生成的上下文信息
     */
    override suspend fun generateEventHandlers(sourceDir: JSZip, context: GenerationContext) {
        // 转换组件列表
        val components = context.components.map { component ->
            val componentImpl = component as ComponentImpl
            // 将ComponentImpl转换回SimbotComponent枚举
            when (componentImpl.simpleId) {
                "qq" -> SimbotComponent.QQ
                "kook" -> SimbotComponent.KOOK
                "ob11" -> SimbotComponent.OB
                else -> SimbotComponent.QQ // 默认值
            }
        }

        when (context.framework) {
            is Framework.Spring -> {
                emitSpringListenerShowcases(context.packageName, components, sourceDir)
            }

            is Framework.Core -> {
                // TODO: 实现Core框架的事件处理器生成
                // 暂时使用空实现，保持向后兼容
            }
        }
    }
}
