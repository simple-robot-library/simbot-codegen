package love.forte.simbot.codegen.gen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import js.date.Date
import jszip.JSZip
import love.forte.codegentle.common.code.*
import love.forte.codegentle.kotlin.KotlinFile
import love.forte.codegentle.kotlin.writeToKotlinString
import love.forte.simbot.codegen.codegen.SimbotComponent
import love.forte.simbot.codegen.codegen.SimbotComponent.*
import love.forte.simbot.codegen.codegen.SpringComponent
import love.forte.simbot.codegen.gen.core.JavaStyle
import love.forte.simbot.codegen.gen.core.ProgrammingLanguage
import org.jetbrains.compose.resources.ExperimentalResourceApi
import simbot_codegen.composeapp.generated.resources.Res


/**
 *
 * Gradle 项目的基本结构:
 *
 * 单模块（JVM）
 * - src
 *   - main
 *     - kotlin
 *       - 源码1.kt
 *       - 源码2.kt
 *       - 源码3.kt
 *     - resources
 * - gradle
 *   - wrapper
 *     - gradle-wrapper.properties
 *   - libs.versions.toml
 * - gradle.properties
 * - build.gradle.kts
 * - settings.gradle.kts
 * - gradlew
 * - gradlew.bat
 * - README.md
 *
 *
 * @author ForteScarlet
 */
class GradleProjectViewModel : ViewModel() {
    var disable: Boolean by mutableStateOf(false)

    var projectName: String by mutableStateOf("simbotProject")
    var projectPackage: String by mutableStateOf("com.example")
    var gradleSettings by mutableStateOf(GradleSettings())

    var simbotVersion: String? by mutableStateOf(null)
    var kotlinVersion: String by mutableStateOf("2.1.20") // TODO initial able?
    
    // 编程语言选择
    var programmingLanguage: ProgrammingLanguage by mutableStateOf(ProgrammingLanguage.Kotlin("2.1.20"))
    
    // Java 样式选择（仅在选择 Java 语言时有效）
    var javaStyle: JavaStyle by mutableStateOf(JavaStyle.BLOCKING)

    // TODO generate
    // var withSpring: Boolean by mutableStateOf(false)
    var withSpring: Boolean
        get() = true
        set(value) {}

    val components: MutableList<SimbotComponentWithVersion> = mutableStateListOf()

//    /**
//     * SpringBoot 组件选择
//     */
//    val selectedSpringComponents: MutableList<SpringComponent> = mutableStateListOf()

    /**
     * SpringBoot 组件选择
     */
    private var selectedSpringComponents: Int by mutableStateOf(0)

    val selectedSpringComponentMask: SpringComponentMask
        get() = SpringComponentMask(selectedSpringComponents)

    value class SpringComponentMask(val mask: Int) {
        operator fun contains(component: SpringComponent): Boolean {
            return (mask and (1 shl component.ordinal)) != 0
        }

        fun count(): Int {
            return mask.countOneBits()
        }

        fun components(): List<SpringComponent> {
            return SpringComponent.entries.filter { component ->
                component in this
            }
        }
    }

    fun addSelectedSpringComponent(component: SpringComponent) {
        selectedSpringComponents = selectedSpringComponents or (1 shl component.ordinal)
    }

    fun removeSelectedSpringComponent(component: SpringComponent) {
        selectedSpringComponents = selectedSpringComponents and (1 shl component.ordinal).inv()
    }

    /**
     * 额外依赖
     */
    val dependencies: MutableList<GradleCatalogVersionDependency> = mutableStateListOf()

    /**
     * 插件，默认先带一个 Kotlin
     */
    val plugins: MutableList<GradleCatalogPlugin> = mutableStateListOf(PLUGIN_KOTLIN)
}

class SimbotComponentWithVersion(
    val component: SimbotComponent,
    version: ComponentVersion = ComponentVersion.UNKNOWN
) {
    var version: ComponentVersion by mutableStateOf(version)

    operator fun component1() = component
    operator fun component2() = version
}

sealed class ComponentVersion {
    data object UNKNOWN : ComponentVersion()
    data class Value(val value: String) : ComponentVersion()
}

class GradleSettings {
    var version: String by mutableStateOf("8.10")
}


@OptIn(ExperimentalResourceApi::class)
suspend fun readGradlew() = Res.readBytes("files/gradle/gradlew").decodeToString()

@OptIn(ExperimentalResourceApi::class)
suspend fun readGradlewBat() = Res.readBytes("files/gradle/gradlew.bat").decodeToString()


fun genGradleWrapperProperties(
    version: String
): String {
    return """
        # ${Date().toISOString()}
        distributionBase=GRADLE_USER_HOME
        distributionPath=wrapper/dists
        distributionUrl=https\://mirrors.cloud.tencent.com/gradle/gradle-${version}-bin.zip
        zipStoreBase=GRADLE_USER_HOME
        zipStorePath=wrapper/dists

    """.trimIndent()
}

fun genGradleBuildScript(
    pkg: String,
    plugins: Iterable<GradleCatalogPlugin>,
    dependencies: Iterable<GradleCatalogVersionDependency>,
): String {
    return KotlinFile {
        addCode {
            inControlFlow("plugins") {
                plugins.forEach { plugin ->
                    addStatement("alias(libs.plugins.%V)") {
                        emitLiteral(plugin.libRefPath)
                    }
                }
            }
        }

        addStatement("group = %V") { emitString(pkg) }
        addStatement("version = %V") { emitString("0.0.1-SNAPSHOT") }
        addStatement("")

        // Java config
        addCode {
            inControlFlow("java") {
                inControlFlow("toolchain") {
                    addStatement("languageVersion = JavaLanguageVersion.of(21)")
                }
            }
        }

        // repositories
        addCode {
            inControlFlow("repositories") {
                addStatement("mavenCentral()")
            }
        }

        // dependencies
        addCode {
            inControlFlow("dependencies") {
                dependencies.forEach { dependency ->
                    addStatement("%V(libs.%V)") {
                        emitLiteral(dependency.configName)
                        emitLiteral(dependency.libRefPath)
                    }
                }
            }
        }

        addCode {
            inControlFlow("kotlin") {
                inControlFlow("compilerOptions") {
                    addStatement("freeCompilerArgs.addAll(%V)") {
                        emitString("-Xjsr305=strict")
                    }
                }
            }
        }

        addCode {
            inControlFlow("tasks.withType<Test>") {
                addStatement("useJUnitPlatform()")
            }
        }
    }.writeToKotlinString()
}

fun genGradleSettingsScript(
    name: String,
): String {
    return KotlinFile {
        addStatement("rootProject.name = %V", CodePart.string(name))
    }.writeToKotlinString()
}

fun genREADME(
    name: String,
    withSpring: Boolean,
    components: Collection<SimbotComponent>,
): String {
    return buildString {
        appendLine("# $name")
        appendLine(
            "这是一个 [Simple Robot](https://github.com/simple-robot) 项目, " +
                    "通过 [Simbot Codegen](https://codegen.simbot.forte.love/) 构建生成。"
        )
        appendLine()
        // 组件说明
        appendLine("## 组件")
        appendLine("添加的组件: ")
        components.forEach { component ->
            appendLine("- [${component.display}](https://github.com/${component.owner}/${component.repo})")
        }
        appendLine()

        components.forEach { component ->
            appendLine("### ${component.display}")
            appendLine("${component.display}组件的配置文件在 `src/main/resources/simbot-bots/${component.simpleId}-example.bot.json` 处。")
            appendLine("你可以参考 [它的配置文档](${component.botConfigDoc}) 了解其详细的配置内容。")
            appendLine()
            appendLine("文件名可以修改, 但是要确保扩展名为 `.bot.json` 。")
            appendLine()
            appendLine("更多可前往 [${component.display}组件手册](${component.doc}) 了解。")
            appendLine()
        }

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
 * 生成并得到 [JSZipApi]
 */
suspend fun doGenerate(project: GradleProjectViewModel): JSZip {
    val name = project.projectName
    val pack = project.projectPackage

    val root = JSZip()
    val rootDir = root.folder(project.projectName)!!
    val sourceDir = rootDir.folder("src/main/kotlin")!!
    val resourceDir = rootDir.folder("src/main/resources")!!

    val simbotVersion = project.simbotVersion?.let { SIMBOT_VERSION.copy(version = it) } ?: SIMBOT_VERSION
    val gradleVersion = project.gradleSettings.version
    val withSpring = project.withSpring
    val components = project.components.toMutableList()
    val deps = project.dependencies.toMutableList()
    for ((component, withVersion) in components) {
        fun GradleCatalogVersionDependency.copyWithVersion(v: ComponentVersion): GradleCatalogVersionDependency {
            return copy(version = version?.copy(version = (v as? ComponentVersion.Value)?.value ?: "<unknown>"))
        }
        when (component) {
            QQ -> {
                deps.add(COMPONENT_QQ.copyWithVersion(withVersion))
            }

            KOOK -> {
                deps.add(COMPONENT_KOOK.copyWithVersion(withVersion))
            }

            OB -> {
                deps.add(COMPONENT_OB_11.copyWithVersion(withVersion))
            }
        }
    }

    if (components.any { it.component.ktorRequired }) {
        deps.add(KTOR_CLIENT_JAVA.copy(configName = "runtimeOnly"))
    }

    val plugins = project.plugins.toMutableList()

    if (withSpring) {
        doGenerateSpring(
            name = name,
            pkg = pack,
            root = rootDir,
            sourceDir = sourceDir,
            resourceDir = resourceDir,
            simbotVersion = simbotVersion,
            gradleVersion = gradleVersion,
            components = components,
            dependencies = deps,
            plugins = plugins,
        )
    } else {
        TODO()
    }

    rootDir.file("gradlew", readGradlew())
    rootDir.file("gradlew.bat", readGradlewBat())
    rootDir.file("gradle.properties", "kotlin.code.style=official")

    return root
}


fun doGenerateSpring(
    name: String,
    pkg: String,
    root: JSZip,
    sourceDir: JSZip,
    resourceDir: JSZip,
    simbotVersion: GradleCatalogVersion,
    gradleVersion: String,
    components: Collection<SimbotComponentWithVersion>,
    dependencies: List<GradleCatalogVersionDependency>,
    plugins: List<GradleCatalogPlugin>,
) {
    val dependencies0 = ArrayList<GradleCatalogVersionDependency>()
    // 追加Spring所需依赖
    dependencies0.add(SPRING_STARTER)
    dependencies0.add(KOTLIN_REFLECT)
    dependencies0.add(SIMBOT_SPRING.copy(version = simbotVersion))
    dependencies0.addAll(dependencies)

    val plugins0 = mutableListOf<GradleCatalogPlugin>()
    plugins0.addAll(plugins)
    // 追加Spring的相关依赖
    plugins0.add(PLUGIN_KOTLIN_SPRING)
    plugins0.add(PLUGIN_SPRING)
    plugins0.add(PLUGIN_SPRING_MANAGEMENT)

    // 示例的配置文件、代码
    emitSpringShowcases(
        projectPackage = pkg,
        components = components.map { it.component },
        sourceSets = sourceDir,
        resources = resourceDir
    )

    // 项目构建脚本、Gradle配置、版本控制
    root.file("gradle/wrapper/gradle-wrapper.properties", genGradleWrapperProperties(gradleVersion))
    root.file(
        "gradle/libs.versions.toml", genGradleCatalogVersion(
            dependencies = dependencies0,
            plugins = plugins0,
        )
    )
    root.file(
        "build.gradle.kts", genGradleBuildScript(
            pkg = pkg,
            plugins = plugins0,
            dependencies = dependencies0
        )
    )
    root.file(
        "settings.gradle.kts", genGradleSettingsScript(name)
    )
    root.file(
        "README.md", genREADME(
            name = name,
            withSpring = true,
            components = components.map { it.component }
        )
    )
}

private val REPLACE_REGEX =
    Regex("^( *public +(class|interface|(abstract|sealed) class|(suspend )?fun|const val|val|var)).*")

/**
 * 清理所有的public访问修饰符：
 * - public class
 * - public interface
 * - public abstract class
 * - public sealed class
 * - public fun
 * - public suspend
 * - public const val
 * - public val
 * - public var
 */
fun String.removeAllPublicModifier(): String {
    return lineSequence()
        .map {
            if (REPLACE_REGEX.matches(it)) {
                it.replaceFirst("public ", "")
            } else {
                it
            }
        }
        .joinToString("\n")
}
