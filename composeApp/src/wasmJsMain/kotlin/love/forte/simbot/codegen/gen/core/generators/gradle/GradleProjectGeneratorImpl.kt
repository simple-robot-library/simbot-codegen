package love.forte.simbot.codegen.gen.core.generators.gradle

import js.date.Date
import jszip.JSZip
import love.forte.simbot.codegen.gen.*
import love.forte.simbot.codegen.gen.core.Dependency
import love.forte.simbot.codegen.gen.core.GenerationContext
import love.forte.simbot.codegen.gen.core.generators.GradleProjectGenerator
import org.jetbrains.compose.resources.ExperimentalResourceApi
import simbot_codegen.composeapp.generated.resources.Res

/**
 * Gradle 项目生成器的实现类。
 *
 * 负责生成 Gradle 项目的基本结构，包括 Gradle 构建脚本、Gradle Wrapper 等。
 *
 * @author ForteScarlet
 */
class GradleProjectGeneratorImpl : GradleProjectGenerator {
    /**
     * 生成 Gradle 构建脚本。
     *
     * @param rootDir 项目根目录的 JSZip 对象
     * @param context 代码生成的上下文信息
     */
    override suspend fun generateBuildScript(rootDir: JSZip, context: GenerationContext) {
        // 将 Dependency 转换为 GradleCatalogVersionDependency
        val catalogDependencies = context.dependencies.map { dependency ->
            love.forte.simbot.codegen.gen.GradleCatalogVersionDependency(
                dependencyName = dependency.name.replace('-', '.'),
                group = dependency.group,
                name = dependency.name,
                version = love.forte.simbot.codegen.gen.GradleCatalogVersion(null, dependency.version),
                configName = dependency.configurationName
            )
        }

        val buildScript = love.forte.simbot.codegen.gen.genGradleBuildScript(
            pkg = context.packageName,
            plugins = getPlugins(context),
            dependencies = catalogDependencies
        )
        rootDir.file("build.gradle.kts", buildScript)
    }

    /**
     * 生成 Gradle 设置脚本。
     *
     * @param rootDir 项目根目录的 JSZip 对象
     * @param context 代码生成的上下文信息
     */
    override suspend fun generateSettingsScript(rootDir: JSZip, context: GenerationContext) {
        val settingsScript = love.forte.simbot.codegen.gen.genGradleSettingsScript(context.projectName)
        rootDir.file("settings.gradle.kts", settingsScript)
    }

    /**
     * 生成 Gradle Wrapper。
     *
     * @param rootDir 项目根目录的 JSZip 对象
     * @param context 代码生成的上下文信息
     */
    override suspend fun generateGradleWrapper(rootDir: JSZip, context: GenerationContext) {
        val gradleVersion = "8.8" // 默认版本，可以从上下文中获取
        rootDir.file("gradle/wrapper/gradle-wrapper.properties", genGradleWrapperProperties(gradleVersion))
        rootDir.file("gradlew", readGradlew())
        rootDir.file("gradlew.bat", readGradlewBat())
    }

    /**
     * 生成 Gradle 属性文件。
     *
     * @param rootDir 项目根目录的 JSZip 对象
     * @param context 代码生成的上下文信息
     */
    override suspend fun generateGradleProperties(rootDir: JSZip, context: GenerationContext) {
        rootDir.file("gradle.properties", "kotlin.code.style=official")
    }

    /**
     * 生成 Gradle 版本目录。
     *
     * @param rootDir 项目根目录的 JSZip 对象
     * @param context 代码生成的上下文信息
     */
    override suspend fun generateVersionCatalog(rootDir: JSZip, context: GenerationContext) {
        val catalogDependencies = context.dependencies.map { dependency ->
            GradleCatalogVersionDependency(
                dependencyName = dependency.name.replace('-', '.'),
                group = dependency.group,
                name = dependency.name,
                version = love.forte.simbot.codegen.gen.GradleCatalogVersion(null, dependency.version),
                configName = dependency.configurationName
            )
        }

        val versionCatalog = genGradleCatalogVersion(
            dependencies = catalogDependencies,
            plugins = getPlugins(context)
        )
        rootDir.file("gradle/libs.versions.toml", versionCatalog)
    }

    /**
     * 生成 README 文件。
     *
     * @param rootDir 项目根目录的 JSZip 对象
     * @param context 代码生成的上下文信息
     */
    suspend fun generateReadme(rootDir: JSZip, context: GenerationContext) {
        val readme = genREADME(
            name = context.projectName,
            withSpring = context.framework is love.forte.simbot.codegen.gen.core.Framework.Spring,
            components = context.components
        )
        rootDir.file("README.md", readme)
    }

    /**
     * 获取插件列表。
     *
     * @param context 代码生成的上下文信息
     * @return 插件列表
     */
    private fun getPlugins(context: GenerationContext): List<love.forte.simbot.codegen.gen.GradleCatalogPlugin> {
        val plugins = mutableListOf<love.forte.simbot.codegen.gen.GradleCatalogPlugin>()

        // 基础插件
        plugins.add(
            love.forte.simbot.codegen.gen.GradleCatalogPlugin(
                pluginName = "kotlin-jvm",
                id = "org.jetbrains.kotlin.jvm",
                version = love.forte.simbot.codegen.gen.GradleCatalogVersion("kotlin", getKotlinVersion(context))
            )
        )

        // 根据框架添加插件
        if (context.framework is love.forte.simbot.codegen.gen.core.Framework.Spring) {
            val springVersion = (context.framework as love.forte.simbot.codegen.gen.core.Framework.Spring).version

            plugins.add(
                love.forte.simbot.codegen.gen.GradleCatalogPlugin(
                    pluginName = "kotlin-plugin-spring",
                    id = "org.jetbrains.kotlin.plugin.spring",
                    version = love.forte.simbot.codegen.gen.GradleCatalogVersion("kotlin", getKotlinVersion(context))
                )
            )

            plugins.add(
                love.forte.simbot.codegen.gen.GradleCatalogPlugin(
                    pluginName = "spring",
                    id = "org.springframework.boot",
                    version = love.forte.simbot.codegen.gen.GradleCatalogVersion("spring", springVersion)
                )
            )

            plugins.add(
                love.forte.simbot.codegen.gen.GradleCatalogPlugin(
                    pluginName = "spring-management",
                    id = "io.spring.dependency-management",
                    version = love.forte.simbot.codegen.gen.GradleCatalogVersion("spring-management", "1.1.6")
                )
            )
        }

        return plugins
    }

    /**
     * 获取 Kotlin 版本。
     *
     * @param context 代码生成的上下文信息
     * @return Kotlin 版本
     */
    private fun getKotlinVersion(context: GenerationContext): String {
        return when (val language = context.language) {
            is love.forte.simbot.codegen.gen.core.ProgrammingLanguage.Kotlin -> language.version
            else -> "2.1.20" // 默认版本
        }
    }

    /**
     * 生成 Gradle 构建脚本。
     *
     * @param pkg 包名
     * @param plugins 插件列表
     * @param dependencies 依赖列表
     * @return 构建脚本内容
     */
    private fun genGradleBuildScript(
        pkg: String,
        plugins: List<GradleCatalogPlugin>,
        dependencies: List<Dependency>
    ): String {
        return fileScriptSpec("build.gradle") {
            inControlFlow("plugins") {
                plugins.forEach { plugin ->
                    addStatement("alias(libs.plugins.%L)", plugin.libRefPath)
                }
            }

            addStatement("group = %S", pkg)
            addStatement("version = %S", "1.0-SNAPSHOT")
            addStatement("")

            inControlFlow("java") {
                addStatement("sourceCompatibility = JavaVersion.VERSION_21")
            }

            // repositories
            inControlFlow("repositories") {
                addStatement("mavenCentral()")
            }

            // dependencies
            inControlFlow("dependencies") {
                dependencies.forEach { dependency ->
                    addStatement("%L(libs.%L)", dependency.configurationName, dependency.name.replace('-', '.'))
                }
            }

            inControlFlow("kotlin") {
                inControlFlow("compilerOptions") {
                    addStatement("freeCompilerArgs.addAll(%S)", "-Xjsr305=strict")
                }
            }

            inControlFlow("tasks.withType<Test>") {
                addStatement("useJUnitPlatform()")
            }

        }.toString().removeAllPublicModifier()
    }

    /**
     * 生成 Gradle 设置脚本。
     *
     * @param name 项目名称
     * @return 设置脚本内容
     */
    private fun genGradleSettingsScript(name: String): String {
        return fileScriptSpec("settings.gradle") {
            addStatement("rootProject.name = %S", name)
        }.toString().removeAllPublicModifier()
    }

    /**
     * 生成 Gradle Wrapper 属性文件。
     *
     * @param version Gradle 版本
     * @return Wrapper 属性文件内容
     */
    private fun genGradleWrapperProperties(version: String): String {
        return """
            # ${Date().toISOString()}
            distributionBase=GRADLE_USER_HOME
            distributionPath=wrapper/dists
            distributionUrl=https\://mirrors.cloud.tencent.com/gradle/gradle-${version}-bin.zip
            zipStoreBase=GRADLE_USER_HOME
            zipStorePath=wrapper/dists
        """.trimIndent()
    }

    /**
     * 生成 README 文件。
     *
     * @param name 项目名称
     * @param withSpring 是否使用 Spring
     * @param components 组件列表
     * @return README 文件内容
     */
    private fun genREADME(
        name: String,
        withSpring: Boolean,
        components: List<love.forte.simbot.codegen.gen.core.Component>
    ): String {
        return buildString {
            appendLine("# $name")
            appendLine("这是一个 [Simple Robot](https://github.com/simple-robot) 项目, 通过 [Simbot Codegen](https://codegen.simbot.forte.love/) 构建生成。")
            appendLine()
            // 组件说明
            appendLine("## 组件")
            appendLine("添加的组件: ")
            components.forEach { component ->
                appendLine("- [${component.name}](https://github.com/${component.name})")
            }
            appendLine()

            // spring 说明
            if (withSpring) {
                appendLine("## Spring")
                appendLine("你选择添加了 [Spring Boot](https://spring.io/projects/spring-boot) 来与simbot集成。")
                appendLine("你可以在 [集成 Spring Boot](https://simbot.forte.love/spring-boot.html) 中了解更多有关集成 Spring Boot 的信息与说明。")
                appendLine()
                appendLine("默认情况下, 依赖中仅添加了一个 `spring-boot-starter` 依赖, 如有需要可自行添加其他所需依赖。")
                appendLine()
                appendLine("Spring Boot 的版本不确保是最新的。如有需要, 可自行修改 Spring Boot 的 Gradle 插件版本。")
                appendLine()
                appendLine(
                    "如果你不添加一些可以确保程序保持运行的 Spring 组件 (例如 `spring-web`), " +
                            "那么你需要修改一下配置文件 `application.yml` 来确保 simbot 可以在后台线程中保持运行: "
                )
                appendLine(
                    """
                    ```yml
                    simbot:
                      application:
                        # 使用一个独立的非守护线程保持程序活跃。
                        application-launch-mode: thread
                    ```
                """.trimIndent()
                )
                appendLine()
            }

            appendLine("## 更多参考")
            appendLine("- 有关 simbot 的更多内容, 你可以前往 [simbot应用手册](https://simbot.forte.love) 了解更多。")
            appendLine("- [文档引导站](https://docs.simbot.forte.love/) 有包括API文档和上述手册在内的所有文档站点的引导。")
            appendLine("- 不熟悉 Kotlin? 前往 [Kotlin 官方文档](https://kotlinlang.org/docs/) 了解学习！")
            appendLine("- simbot 支持 [Kotlin 多平台](https://kotlinlang.org/docs/multiplatform.html) 。")
            appendLine("  如有需要, 可修改为多平台项目。")
            if (withSpring) {
                appendLine("- [Spring Boot](https://spring.io/projects/spring-boot)")
            }
        }
    }

    /**
     * 读取 gradlew 文件内容。
     *
     * @return gradlew 文件内容
     */
    @OptIn(ExperimentalResourceApi::class)
    private suspend fun readGradlew(): String {
        return Res.readBytes("files/gradle/gradlew").decodeToString()
    }

    /**
     * 读取 gradlew.bat 文件内容。
     *
     * @return gradlew.bat 文件内容
     */
    @OptIn(ExperimentalResourceApi::class)
    private suspend fun readGradlewBat(): String {
        return Res.readBytes("files/gradle/gradlew.bat").decodeToString()
    }
}

