package love.forte.simbot.codegen.gen.core.generators.gradle

import js.date.Date
import jszip.JSZip
import love.forte.simbot.codegen.gen.*
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
            dependency.catalog ?: GradleCatalogVersionDependency(
                dependencyName = dependency.name.replace('.', '-'),
                group = dependency.group,
                name = dependency.name,
                version = GradleCatalogVersion(null, dependency.version),
                configName = dependency.configurationName
            )
        }

        val buildScript = genGradleBuildScript(
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
        val settingsScript = genGradleSettingsScript(context.projectName)
        rootDir.file("settings.gradle.kts", settingsScript)
    }

    /**
     * 生成 Gradle Wrapper。
     *
     * @param rootDir 项目根目录的 JSZip 对象
     * @param context 代码生成的上下文信息
     */
    override suspend fun generateGradleWrapper(rootDir: JSZip, context: GenerationContext) {
        val gradleVersion = context.gradleVersion
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
            dependency.catalog ?: GradleCatalogVersionDependency(
                dependencyName = dependency.name.replace('.', '-'),
                group = dependency.group,
                name = dependency.name,
                version = GradleCatalogVersion(null, dependency.version),
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
     * 获取插件列表。
     *
     * @param context 代码生成的上下文信息
     * @return 插件列表
     */
    private fun getPlugins(context: GenerationContext): List<GradleCatalogPlugin> {
        val plugins = mutableListOf<GradleCatalogPlugin>()

        // 基础插件
        plugins.add(
            GradleCatalogPlugin(
                pluginName = "kotlin-jvm",
                id = "org.jetbrains.kotlin.jvm",
                version = GradleCatalogVersion("kotlin", getKotlinVersion(context))
            )
        )

        // 根据框架添加插件
        if (context.framework is love.forte.simbot.codegen.gen.core.Framework.Spring) {
            val springVersion = (context.framework as love.forte.simbot.codegen.gen.core.Framework.Spring).version

            plugins.add(
                GradleCatalogPlugin(
                    pluginName = "kotlin-plugin-spring",
                    id = "org.jetbrains.kotlin.plugin.spring",
                    version = GradleCatalogVersion("kotlin", getKotlinVersion(context))
                )
            )

            plugins.add(
                GradleCatalogPlugin(
                    pluginName = "spring",
                    id = "org.springframework.boot",
                    version = GradleCatalogVersion("spring", springVersion)
                )
            )

            plugins.add(
                GradleCatalogPlugin(
                    pluginName = "spring-management",
                    id = "io.spring.dependency-management",
                    version = GradleCatalogVersion("spring-management", "1.1.6")
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

