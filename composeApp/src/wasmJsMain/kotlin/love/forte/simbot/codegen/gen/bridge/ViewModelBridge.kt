package love.forte.simbot.codegen.gen.bridge

import jszip.JSZip
import love.forte.simbot.codegen.gen.*
import love.forte.simbot.codegen.gen.core.CodeGenerator
import love.forte.simbot.codegen.gen.core.Framework
import love.forte.simbot.codegen.gen.core.ProgrammingLanguage
import love.forte.simbot.codegen.gen.core.context.ComponentImpl
import love.forte.simbot.codegen.gen.core.context.DependencyImpl
import love.forte.simbot.codegen.gen.core.context.createGenerationContext
import love.forte.simbot.codegen.gen.core.generators.ProjectGeneratorFactory

/**
 * 视图模型桥接器。
 * 
 * 将现有的 [GradleProjectViewModel] 与新的代码生成架构桥接起来。
 * 
 * @author ForteScarlet
 */
class ViewModelBridge(
    /**
     * 项目生成器工厂。
     */
    private val generatorFactory: ProjectGeneratorFactory
) {
    /**
     * 从视图模型生成项目。
     * 
     * @param viewModel 视图模型
     * @return 生成的项目的 JSZip 对象
     */
    suspend fun generateProject(viewModel: GradleProjectViewModel): JSZip {
        // 创建生成上下文
        val context = createGenerationContext {
            projectName = viewModel.projectName
            packageName = viewModel.projectPackage
            language = viewModel.programmingLanguage
            framework = if (viewModel.withSpring) {
                Framework.Spring("3.3.3") // 使用默认版本，后续可以从视图模型中获取
            } else {
                Framework.Core
            }
            
            // 添加组件
            components.addAll(viewModel.components.map { componentWithVersion ->
                val (component, version) = componentWithVersion
                ComponentImpl(
                    name = component.name,
                    version = when (version) {
                        is ComponentVersion.Value -> version.value
                        else -> "latest" // 使用默认版本
                    },
                    simpleId = component.simpleId,
                    display = component.display,
                    owner = component.owner,
                    repo = component.repo,
                    doc = component.doc,
                    botConfigDoc = component.botConfigDoc
                )
            })
            
            // 添加依赖
            dependencies.addAll(viewModel.dependencies.map { dependency ->
                DependencyImpl(
                    group = dependency.group,
                    name = dependency.name,
                    version = dependency.version?.version ?: "latest",
                    configurationName = dependency.configName
                )
            })
            
            // 添加必要的 simbot 依赖
            if (framework is Framework.Spring) {
                // Spring 相关依赖
                dependencies.add(
                    DependencyImpl(
                        group = SPRING_STARTER.group,
                        name = SPRING_STARTER.name,
                        version = SPRING_VERSION.version,
                        configurationName = SPRING_STARTER.configName
                    )
                )
                
                dependencies.add(
                    DependencyImpl(
                        group = KOTLIN_REFLECT.group,
                        name = KOTLIN_REFLECT.name,
                        version = KOTLIN_REFLECT.version?.version ?: "latest",
                        configurationName = KOTLIN_REFLECT.configName
                    )
                )
                
                dependencies.add(
                    DependencyImpl(
                        group = SIMBOT_SPRING.group,
                        name = SIMBOT_SPRING.name,
                        version = viewModel.simbotVersion ?: SIMBOT_SPRING.version?.version ?: "latest",
                        configurationName = SIMBOT_SPRING.configName
                    )
                )
            } else {
                // 核心库依赖
                dependencies.add(
                    DependencyImpl(
                        group = SIMBOT_CORE.group,
                        name = SIMBOT_CORE.name,
                        version = viewModel.simbotVersion ?: SIMBOT_CORE.version?.version ?: "latest",
                        configurationName = SIMBOT_CORE.configName
                    )
                )
            }
            
            // 添加组件依赖
            for ((component, componentVersion) in viewModel.components) {
                val componentDependency = when (component) {
                    SimbotComponent.QQ -> COMPONENT_QQ
                    SimbotComponent.KOOK -> COMPONENT_KOOK
                    SimbotComponent.OB -> COMPONENT_OB_11
                }
                
                dependencies.add(
                    DependencyImpl(
                        group = componentDependency.group,
                        name = componentDependency.name,
                        version = (componentVersion as? ComponentVersion.Value)?.value ?: componentDependency.version?.version ?: "latest",
                        configurationName = componentDependency.configName
                    )
                )
            }
            
            // 如果有组件需要 Ktor，添加 Ktor 依赖
            if (viewModel.components.any { it.component.ktorRequired }) {
                dependencies.add(
                    DependencyImpl(
                        group = KTOR_CLIENT_JAVA.group,
                        name = KTOR_CLIENT_JAVA.name,
                        version = KTOR_CLIENT_JAVA.version?.version ?: "latest",
                        configurationName = "runtimeOnly"
                    )
                )
            }
        }
        
        // 创建生成器
        val generator = generatorFactory.createGenerator(context)
        
        // 生成项目
        val root = JSZip()
        generator.generate(root, context)
        
        return root
    }
}

/**
 * 将 [SimbotComponent] 转换为 [ComponentImpl]。
 * 
 * @param version 组件版本
 * @return 转换后的 [ComponentImpl]
 */
fun SimbotComponent.toComponentImpl(version: String): ComponentImpl {
    return ComponentImpl(
        name = this.name,
        version = version,
        simpleId = this.simpleId,
        display = this.display,
        owner = this.owner,
        repo = this.repo,
        doc = this.doc,
        botConfigDoc = this.botConfigDoc
    )
}

/**
 * 将 [SimbotComponentWithVersion] 转换为 [ComponentImpl]。
 * 
 * @return 转换后的 [ComponentImpl]
 */
fun SimbotComponentWithVersion.toComponentImpl(): ComponentImpl {
    val versionStr = when (val v = this.version) {
        is ComponentVersion.Value -> v.value
        else -> "latest" // 使用默认版本
    }
    return this.component.toComponentImpl(versionStr)
}
