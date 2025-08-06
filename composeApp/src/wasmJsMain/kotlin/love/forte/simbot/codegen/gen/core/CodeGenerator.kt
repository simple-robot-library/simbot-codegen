package love.forte.simbot.codegen.gen.core

import jszip.JSZip

/**
 * 代码生成器的核心接口。
 * 
 * 代码生成器负责生成特定类型的代码，例如项目结构、源代码文件、配置文件等。
 * 不同的代码生成器可以组合使用，以生成完整的项目。
 * 
 * @author ForteScarlet
 */
interface CodeGenerator {
    /**
     * 生成代码并将结果写入指定的 [JSZip] 对象。
     * 
     * @param zip 用于存储生成的文件的 JSZip 对象
     * @param context 代码生成的上下文信息
     */
    suspend fun generate(zip: JSZip, context: GenerationContext)
}

/**
 * 代码生成的上下文信息。
 * 
 * 上下文包含生成代码所需的所有配置和状态信息。
 */
interface GenerationContext {
    /**
     * 项目名称
     */
    val projectName: String
    
    /**
     * 项目包名
     */
    val packageName: String
    
    /**
     * 项目语言
     */
    val language: ProgrammingLanguage
    
    /**
     * 项目框架
     */
    val framework: Framework
    
    /**
     * 项目组件
     */
    val components: List<Component>
    
    /**
     * 项目依赖
     */
    val dependencies: List<Dependency>
}

/**
 * 编程语言。
 * 
 * 定义了项目使用的编程语言，例如 Kotlin 或 Java。
 */
sealed class ProgrammingLanguage {
    /**
     * 语言的名称
     */
    abstract val name: String
    
    /**
     * 语言的文件扩展名
     */
    abstract val fileExtension: String
    
    /**
     * Kotlin 语言
     */
    data class Kotlin(
        /**
         * Kotlin 版本
         */
        val version: String
    ) : ProgrammingLanguage() {
        override val name: String = "Kotlin"
        override val fileExtension: String = "kt"
    }
    
    /**
     * Java 语言
     */
    data class Java(
        /**
         * Java 版本
         */
        val version: String,
        
        /**
         * API 风格：阻塞式或异步式
         */
        val style: JavaStyle
    ) : ProgrammingLanguage() {
        override val name: String = "Java"
        override val fileExtension: String = "java"
    }
}

/**
 * Java API 风格
 */
enum class JavaStyle {
    /**
     * 阻塞式 API
     */
    BLOCKING,
    
    /**
     * 异步式 API
     */
    ASYNC
}

/**
 * 框架。
 * 
 * 定义了项目使用的框架，例如 Spring 或非 Spring。
 */
sealed class Framework {
    /**
     * 框架的名称
     */
    abstract val name: String
    
    /**
     * Spring 框架
     */
    data class Spring(
        /**
         * Spring Boot 版本
         */
        val version: String
    ) : Framework() {
        override val name: String = "Spring"
    }
    
    /**
     * 非 Spring 框架（核心库）
     */
    data object Core : Framework() {
        override val name: String = "Core"
    }
}

/**
 * 组件。
 * 
 * 定义了项目使用的 Simbot 组件，例如 QQ、KOOK 等。
 */
interface Component {
    /**
     * 组件的名称
     */
    val name: String
    
    /**
     * 组件的版本
     */
    val version: String
}

/**
 * 依赖。
 * 
 * 定义了项目的依赖，例如库、框架等。
 */
interface Dependency {
    /**
     * 依赖的组 ID
     */
    val group: String
    
    /**
     * 依赖的名称
     */
    val name: String
    
    /**
     * 依赖的版本
     */
    val version: String
    
    /**
     * 依赖的配置名称，例如 implementation、api 等
     */
    val configurationName: String
}
