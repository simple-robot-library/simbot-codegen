package love.forte.simbot.codegen.gen.core.generators

import jszip.JSZip
import love.forte.simbot.codegen.gen.core.CodeGenerator
import love.forte.simbot.codegen.gen.core.GenerationContext

/**
 * 复合代码生成器。
 *
 * 将多个代码生成器组合在一起，按顺序执行它们。
 *
 * @author ForteScarlet
 */
class CompositeGenerator(
    /**
     * 要组合的代码生成器列表。
     */
    private val generators: List<CodeGenerator>
) : CodeGenerator {
    /**
     * 按顺序执行所有代码生成器。
     *
     * @param zip 用于存储生成的文件的 JSZip 对象
     * @param context 代码生成的上下文信息
     */
    override suspend fun generate(zip: JSZip, context: GenerationContext) {
        generators.forEach { generator ->
            generator.generate(zip, context)
        }
    }

    /**
     * 创建一个新的复合代码生成器，添加指定的代码生成器。
     *
     * @param generator 要添加的代码生成器
     * @return 新的复合代码生成器
     */
    operator fun plus(generator: CodeGenerator): CompositeGenerator {
        return CompositeGenerator(generators + generator)
    }

    companion object {
        /**
         * 创建一个空的复合代码生成器。
         *
         * @return 空的复合代码生成器
         */
        fun empty(): CompositeGenerator = CompositeGenerator(emptyList())

        /**
         * 创建一个包含单个代码生成器的复合代码生成器。
         *
         * @param generator 代码生成器
         * @return 包含单个代码生成器的复合代码生成器
         */
        fun of(generator: CodeGenerator): CompositeGenerator = CompositeGenerator(listOf(generator))

        /**
         * 创建一个包含多个代码生成器的复合代码生成器。
         *
         * @param generators 代码生成器列表
         * @return 包含多个代码生成器的复合代码生成器
         */
        fun of(vararg generators: CodeGenerator): CompositeGenerator = CompositeGenerator(generators.toList())
    }
}

/**
 * 项目生成器工厂。
 *
 * 负责创建适合特定项目配置的代码生成器。
 */
interface ProjectGeneratorFactory {
    /**
     * 创建适合特定项目配置的代码生成器。
     *
     * @param context 代码生成的上下文信息
     * @return 适合特定项目配置的代码生成器
     */
    fun createGenerator(context: GenerationContext): CodeGenerator
}

/**
 * 基于语言和框架的项目生成器工厂。
 *
 * 根据项目的语言和框架选择合适的代码生成器。
 */
class LanguageAndFrameworkBasedGeneratorFactory(
    /**
     * 项目结构生成器工厂。
     */
    private val projectGeneratorFactory: (GenerationContext) -> ProjectGenerator,

    /**
     * Kotlin 源代码生成器工厂。
     */
    private val kotlinSourceGeneratorFactory: (GenerationContext) -> KotlinSourceCodeGenerator,

    /**
     * Java 源代码生成器工厂。
     */
    private val javaSourceGeneratorFactory: (GenerationContext) -> JavaSourceCodeGenerator,

    /**
     * Spring 配置生成器工厂。
     */
    private val springConfigGeneratorFactory: (GenerationContext) -> SpringConfigurationGenerator,

    /**
     * 核心库配置生成器工厂。
     */
    private val coreConfigGeneratorFactory: (GenerationContext) -> CoreConfigurationGenerator
) : ProjectGeneratorFactory {
    /**
     * 根据项目的语言和框架选择合适的代码生成器。
     *
     * @param context 代码生成的上下文信息
     * @return 适合特定项目配置的代码生成器
     */
    override fun createGenerator(context: GenerationContext): CodeGenerator {
        val projectGenerator = projectGeneratorFactory(context)

        val sourceGenerator = when (context.language) {
            is love.forte.simbot.codegen.gen.core.ProgrammingLanguage.Kotlin -> kotlinSourceGeneratorFactory(context)
            is love.forte.simbot.codegen.gen.core.ProgrammingLanguage.Java -> javaSourceGeneratorFactory(context)
        }

        val configGenerator = when (context.framework) {
            is love.forte.simbot.codegen.gen.core.Framework.Spring -> springConfigGeneratorFactory(context)
            is love.forte.simbot.codegen.gen.core.Framework.Core -> coreConfigGeneratorFactory(context)
        }

        return CompositeGenerator.of(
            projectGenerator,
            sourceGenerator,
            configGenerator
        )
    }
}
