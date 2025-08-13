package love.forte.simbot.codegen.gen.core.bridge

import jszip.JSZip
import love.forte.simbot.codegen.gen.*
import love.forte.simbot.codegen.gen.core.*
import love.forte.simbot.codegen.gen.core.context.ComponentImpl
import love.forte.simbot.codegen.gen.core.context.DependencyImpl
import love.forte.simbot.codegen.gen.core.context.createGenerationContext
import love.forte.simbot.codegen.gen.core.context.toDependency
import love.forte.simbot.codegen.gen.core.generators.ProjectGeneratorFactory

/**
 * 视图模型桥接器。
 * 
 * 将现有的视图模型与新的代码生成架构桥接，实现从 [GradleProjectViewModel] 到 [GenerationContext] 的转换。
 * 
 * @author ForteScarlet
 */
class ViewModelBridge(
    /**
     * 项目生成器工厂
     */
    private val generatorFactory: ProjectGeneratorFactory
) {
    /**
     * 生成项目。
     * 
     * @param viewModel 视图模型
     * @return 生成的项目文件
     */
    suspend fun generateProject(viewModel: GradleProjectViewModel): JSZip {
        val context = createGenerationContextFromViewModel(viewModel)
        val generator = generatorFactory.createGenerator(context)
        val root = JSZip()
        generator.generate(root, context)
        return root
    }
    
    /**
     * 从视图模型创建生成上下文。
     * 
     * @param viewModel 视图模型
     * @return 生成上下文
     */
    private fun createGenerationContextFromViewModel(viewModel: GradleProjectViewModel): GenerationContext {
        return createGenerationContext {
            // 基本信息
            projectName = viewModel.projectName
            packageName = viewModel.projectPackage
            
            // 语言
            language = viewModel.programmingLanguage
            
            // 框架
            framework = if (viewModel.withSpring) {
                Framework.Spring(SPRING_VERSION.version)
            } else {
                Framework.Core
            }
            
            // 组件
            components = viewModel.components.map { (component, version) ->
                ComponentImpl(
                    name = component.name,
                    version = (version as? ComponentVersion.Value)?.value ?: "latest",
                    simpleId = component.simpleId,
                    display = component.display,
                    owner = component.owner,
                    repo = component.repo,
                    doc = component.doc,
                    botConfigDoc = component.botConfigDoc
                )
            }.toMutableList()
            
            // 依赖
            dependencies = viewModel.dependencies.map { dependency ->
                dependency.toDependency()
            }.toMutableList()
            
            // 添加必要的依赖
            if (framework is Framework.Spring) {
                // Spring 相关依赖
                dependencies.add(SPRING_STARTER.toDependency())
                dependencies.add(KOTLIN_REFLECT.toDependency())
                dependencies.add(SIMBOT_SPRING.toDependency(viewModel.simbotVersion))
            } else {
                // 核心库依赖
                dependencies.add(SIMBOT_CORE.toDependency(viewModel.simbotVersion))
            }
            
            // 如果有组件需要 Ktor，添加 Ktor 依赖
            if (viewModel.components.any { it.component.ktorRequired }) {
                dependencies.add(KTOR_CLIENT_JAVA.toDependency(configurationName = "runtimeOnly"))
            }
        }
    }
}
