# Simbot Codegen 代码生成模块改进日志

## 当前架构分析

### 主要组件

1. **GradleProjectViewModel** (`GradleProject.kt`)
   - 负责存储项目配置数据，如项目名称、包名、Gradle设置等
   - 包含组件选择和依赖管理

2. **代码生成函数** (`GradleProject.kt`)
   - `doGenerate`: 主要的代码生成入口，协调整个生成过程
   - `doGenerateSpring`: 专门用于生成Spring集成项目
   - 目前没有实现非Spring项目的生成 (存在TODO)

3. **示例代码生成** (`Showcases.kt`)
   - `emitSpringShowcases`: 生成Spring集成的示例代码
   - `genCoreSourceShowcases`: 计划用于生成核心库示例，但尚未实现 (存在TODO)

4. **组件和依赖管理** (`ComponentDependencies.kt`, `SimbotComponent.kt`, `CatalogVersion.kt`)
   - 定义了可用的Simbot组件 (QQ, KOOK, OneBot)
   - 管理依赖和版本信息
   - 生成Gradle版本目录 (`libs.versions.toml`)

5. **工具函数** (`Extensions.kt`)
   - 提供用于代码生成的工具函数，主要是KotlinPoet的扩展

### 当前架构的局限性

1. **紧耦合的代码生成逻辑**
   - 代码生成逻辑集中在少数几个大型函数中，难以扩展和维护
   - 没有清晰的抽象来分离不同类型的生成器 (语言、框架等)

2. **缺乏语言支持**
   - 目前只支持Kotlin语言，没有Java语言的支持
   - 没有为不同语言风格 (阻塞式、异步式) 提供抽象

3. **框架集成限制**
   - 紧耦合到Spring框架，非Spring集成尚未实现
   - 没有清晰的框架抽象

4. **示例代码生成混合**
   - 示例代码生成与项目结构生成混合在一起
   - 难以为不同的语言和框架组合提供示例

## 改进计划

### 1. 重构代码生成架构

设计一个更加模块化、可扩展的架构，主要包括：

- **核心接口和抽象类**：定义代码生成的基本接口和抽象类
- **语言生成器**：负责特定语言的代码生成 (Kotlin, Java)
- **框架生成器**：负责特定框架的集成 (Spring, 非Spring)
- **项目结构生成器**：负责生成项目的基本结构
- **示例代码生成器**：负责生成示例代码

### 2. 实现语言生成器

- **Kotlin生成器**：重构现有的Kotlin代码生成
- **Java生成器**：实现Java代码生成，包括阻塞式和异步式API

### 3. 实现框架生成器

- **Spring生成器**：重构现有的Spring集成代码生成
- **非Spring生成器**：实现非Spring集成的代码生成

### 4. 改进示例代码生成

- 将示例代码生成与项目结构生成分离
- 为不同的语言和框架组合提供示例

### 5. 添加中文文档

- 在关键类和方法上添加简洁明了的中文文档
- 说明各个组件的职责和使用方法

## 新架构设计

### 核心接口和抽象类

1. **CodeGenerator**：代码生成器的核心接口，定义了生成代码的基本方法
   ```kotlin
   interface CodeGenerator {
       suspend fun generate(zip: JSZip, context: GenerationContext)
   }
   ```

2. **GenerationContext**：代码生成的上下文信息，包含项目配置
   ```kotlin
   interface GenerationContext {
       val projectName: String
       val packageName: String
       val language: ProgrammingLanguage
       val framework: Framework
       val components: List<Component>
       val dependencies: List<Dependency>
   }
   ```

3. **ProgrammingLanguage**：编程语言的抽象类，支持Kotlin和Java
   ```kotlin
   sealed class ProgrammingLanguage {
       abstract val name: String
       abstract val fileExtension: String
       
       data class Kotlin(val version: String) : ProgrammingLanguage()
       data class Java(val version: String, val style: JavaStyle) : ProgrammingLanguage()
   }
   ```

4. **Framework**：框架的抽象类，支持Spring和非Spring
   ```kotlin
   sealed class Framework {
       abstract val name: String
       
       data class Spring(val version: String) : Framework()
       data object Core : Framework()
   }
   ```

### 专用生成器接口

1. **ProjectGenerator**：项目结构生成器
   ```kotlin
   interface ProjectGenerator : CodeGenerator {
       suspend fun generateProjectStructure(rootDir: JSZip, context: GenerationContext)
   }
   ```

2. **SourceCodeGenerator**：源代码生成器
   ```kotlin
   interface SourceCodeGenerator : CodeGenerator {
       suspend fun generateSourceCode(sourceDir: JSZip, context: GenerationContext)
   }
   ```

3. **ConfigurationGenerator**：配置文件生成器
   ```kotlin
   interface ConfigurationGenerator : CodeGenerator {
       suspend fun generateConfigurations(resourceDir: JSZip, context: GenerationContext)
   }
   ```

### 组合模式

1. **CompositeGenerator**：复合代码生成器，组合多个生成器
   ```kotlin
   class CompositeGenerator(private val generators: List<CodeGenerator>) : CodeGenerator {
       override suspend fun generate(zip: JSZip, context: GenerationContext) {
           generators.forEach { it.generate(zip, context) }
       }
   }
   ```

2. **ProjectGeneratorFactory**：项目生成器工厂，根据上下文创建合适的生成器
   ```kotlin
   interface ProjectGeneratorFactory {
       fun createGenerator(context: GenerationContext): CodeGenerator
   }
   ```

### 桥接模式

**ViewModelBridge**：将现有的视图模型与新的代码生成架构桥接
```kotlin
class ViewModelBridge(private val generatorFactory: ProjectGeneratorFactory) {
    suspend fun generateProject(viewModel: GradleProjectViewModel): JSZip {
        val context = createGenerationContext { /* 配置上下文 */ }
        val generator = generatorFactory.createGenerator(context)
        val root = JSZip()
        generator.generate(root, context)
        return root
    }
}
```

## 实施进度

### 2025-08-06

- [x] 分析当前代码生成架构
- [x] 创建改进日志文档
- [x] 设计新的代码生成架构
- [x] 实现核心接口和抽象类
  - [x] 创建 `CodeGenerator` 接口
  - [x] 创建 `GenerationContext` 接口
  - [x] 创建 `ProgrammingLanguage` 和 `Framework` 抽象类
  - [x] 创建专用生成器接口 (`ProjectGenerator`, `SourceCodeGenerator`, `ConfigurationGenerator`)
  - [x] 创建复合生成器和工厂
  - [x] 创建视图模型桥接器
- [ ] 重构Kotlin语言生成器
- [ ] 实现Java语言生成器
- [ ] 重构Spring框架生成器
- [ ] 实现非Spring框架生成器
- [ ] 改进示例代码生成
- [x] 添加中文文档
- [ ] 测试新的代码生成架构

### 2025-08-07

- [x] 检查已实现的核心接口和抽象类
  - [x] 检查 `CodeGenerator` 接口实现
  - [x] 检查 `GenerationContext` 接口实现
  - [x] 检查 `ProgrammingLanguage` 和 `Framework` 抽象类实现
  - [x] 检查专用生成器接口实现
  - [x] 检查复合生成器和工厂实现
  - [x] 检查视图模型桥接器实现
- [x] 检查项目结构生成器实现
  - [x] 检查 `ProjectGenerator` 接口实现
  - [x] 检查 `GradleProjectGenerator` 接口实现
  - [x] 检查 `GradleProjectGeneratorImpl` 实现
- [x] 检查现有的代码生成逻辑
  - [x] 检查 Kotlin 源代码生成逻辑
  - [x] 检查 Spring 配置生成逻辑
  - [x] 检查示例代码生成逻辑
- [ ] 尝试实现 KotlinSourceCodeGenerator 的具体实现类
  - [x] 分析现有的 Kotlin 源代码生成逻辑
  - [x] 检查项目中的 KotlinPoet 扩展函数
  - [ ] 创建 KotlinSourceCodeGeneratorImpl 类（遇到问题）
- [x] 实现框架相关的生成器
  - [x] 创建 SpringConfigurationGeneratorImpl 类
  - [x] 创建 CoreConfigurationGeneratorImpl 类

## 遇到的问题

### KotlinSourceCodeGeneratorImpl 实现问题

在尝试实现 KotlinSourceCodeGeneratorImpl 类时，遇到了以下问题：

1. **KotlinPoet API 使用问题**：
   - 项目中使用了自定义的 KotlinPoet 扩展函数，如 `fileSpec`、`addClass`、`addFunction` 等
   - 这些扩展函数的使用方式与标准的 KotlinPoet API 有所不同
   - 尝试直接使用这些扩展函数时遇到了编译错误

2. **DSL 风格 API 不一致**：
   - 项目中的 KotlinPoet 扩展函数使用了 DSL 风格的 API
   - 在尝试使用这些 DSL 风格的 API 时，遇到了类型不匹配和方法未找到的错误

### 配置生成器实现

由于在实现 KotlinSourceCodeGeneratorImpl 类时遇到了问题，我们转向了实现相对简单的配置生成器：

1. **SpringConfigurationGeneratorImpl**：
   - 实现了 SpringConfigurationGenerator 接口
   - 负责生成 Spring 框架的配置文件，包括应用程序配置文件（application.yml）和组件配置文件
   - 使用字符串模板直接生成配置文件内容，避免了 KotlinPoet 的复杂性
   - 支持为不同的组件（QQ、KOOK、OneBot）生成不同的配置文件

2. **CoreConfigurationGeneratorImpl**：
   - 实现了 CoreConfigurationGenerator 接口
   - 负责生成核心库（非 Spring）的配置文件，主要是组件配置文件
   - 与 SpringConfigurationGeneratorImpl 类似，使用字符串模板生成配置文件内容
   - 目前核心库不需要特殊的配置文件，但保留了扩展的可能性

这两个配置生成器的实现相对简单，因为它们不需要使用 KotlinPoet，而是直接使用字符串模板来生成配置文件内容。这避免了我们在尝试实现 KotlinSourceCodeGeneratorImpl 类时遇到的问题。

### 后续计划

1. **深入研究项目中的 KotlinPoet 使用方式**：
   - 更详细地分析 `Showcases.kt` 和其他使用 KotlinPoet 的文件
   - 理解项目中 KotlinPoet 扩展函数的具体实现和使用方式

2. **分阶段实现生成器**：
   - 先实现简单的生成器，如 ConfigurationGenerator
   - 逐步理解和掌握项目中的 KotlinPoet 使用方式
   - 然后再实现更复杂的生成器，如 KotlinSourceCodeGenerator

3. **考虑替代方案**：
   - 如果直接使用 KotlinPoet 扩展函数仍然困难，考虑使用更直接的方式生成代码
   - 例如，使用模板字符串或者直接复用现有的代码生成函数

## 总结与下一步计划

### 2025-08-07 总结

今天的主要成果：

1. **完成了核心接口和抽象类的检查**：
   - 确认了 CodeGenerator、GenerationContext 等核心接口的实现
   - 确认了 ProgrammingLanguage、Framework 等抽象类的实现
   - 确认了专用生成器接口、复合生成器和工厂的实现

2. **完成了项目结构生成器的检查**：
   - 确认了 ProjectGenerator 接口和 GradleProjectGenerator 接口的实现
   - 确认了 GradleProjectGeneratorImpl 的实现

3. **实现了框架相关的生成器**：
   - 创建了 SpringConfigurationGeneratorImpl 类，实现了 SpringConfigurationGenerator 接口
   - 创建了 CoreConfigurationGeneratorImpl 类，实现了 CoreConfigurationGenerator 接口

4. **尝试实现语言相关的生成器**：
   - 分析了现有的 Kotlin 源代码生成逻辑
   - 检查了项目中的 KotlinPoet 扩展函数
   - 尝试创建 KotlinSourceCodeGeneratorImpl 类，但遇到了问题

### 下一步计划

1. **深入研究 KotlinPoet 使用方式**：
   - 更详细地分析项目中 KotlinPoet 的使用方式
   - 理解项目中 KotlinPoet 扩展函数的具体实现和使用方式

2. **继续实现语言相关的生成器**：
   - 尝试使用不同的方式实现 KotlinSourceCodeGenerator
   - 考虑使用模板字符串或直接复用现有的代码生成函数

3. **改进示例代码生成**：
   - 创建示例代码生成器接口
   - 为不同的语言和框架组合创建示例

4. **实现 UI 选择入口**：
   - 为语言选择预留入口
   - 为框架选择预留入口
   - 为 API 风格选择预留入口

5. **测试新的代码生成架构**：
   - 确保所有组件能够正常工作
   - 验证生成的代码是否符合预期
