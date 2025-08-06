package love.forte.simbot.codegen.gen.core.context

import love.forte.simbot.codegen.gen.core.*

/**
 * [GenerationContext] 的实现类。
 * 
 * 用于存储项目配置数据并传递给代码生成器。
 * 
 * @author ForteScarlet
 */
data class GenerationContextImpl(
    override val projectName: String,
    override val packageName: String,
    override val language: ProgrammingLanguage,
    override val framework: Framework,
    override val components: List<Component>,
    override val dependencies: List<Dependency>
) : GenerationContext

/**
 * 创建一个 [GenerationContextImpl] 的构建器。
 */
class GenerationContextBuilder {
    /**
     * 项目名称
     */
    var projectName: String = "simbotProject"
    
    /**
     * 项目包名
     */
    var packageName: String = "com.example"
    
    /**
     * 项目语言
     */
    var language: ProgrammingLanguage = ProgrammingLanguage.Kotlin("2.1.20")
    
    /**
     * 项目框架
     */
    var framework: Framework = Framework.Spring("3.3.3")
    
    /**
     * 项目组件
     */
    var components: MutableList<Component> = mutableListOf()
    
    /**
     * 项目依赖
     */
    var dependencies: MutableList<Dependency> = mutableListOf()
    
    /**
     * 构建 [GenerationContextImpl]。
     */
    fun build(): GenerationContextImpl {
        return GenerationContextImpl(
            projectName = projectName,
            packageName = packageName,
            language = language,
            framework = framework,
            components = components.toList(),
            dependencies = dependencies.toList()
        )
    }
}

/**
 * 创建一个 [GenerationContextImpl]。
 * 
 * @param block 配置构建器的代码块
 * @return 创建的 [GenerationContextImpl]
 */
fun createGenerationContext(block: GenerationContextBuilder.() -> Unit): GenerationContextImpl {
    val builder = GenerationContextBuilder()
    builder.block()
    return builder.build()
}

/**
 * [Component] 的实现类。
 * 
 * @property name 组件的名称
 * @property version 组件的版本
 * @property simpleId 组件的简单标识符
 * @property display 组件的显示名称
 * @property owner 组件的所有者
 * @property repo 组件的仓库
 * @property doc 组件的文档链接
 * @property botConfigDoc 组件的机器人配置文档链接
 */
data class ComponentImpl(
    override val name: String,
    override val version: String,
    val simpleId: String,
    val display: String,
    val owner: String,
    val repo: String,
    val doc: String,
    val botConfigDoc: String
) : Component

/**
 * [Dependency] 的实现类。
 * 
 * @property group 依赖的组 ID
 * @property name 依赖的名称
 * @property version 依赖的版本
 * @property configurationName 依赖的配置名称
 */
data class DependencyImpl(
    override val group: String,
    override val name: String,
    override val version: String,
    override val configurationName: String = "implementation"
) : Dependency
