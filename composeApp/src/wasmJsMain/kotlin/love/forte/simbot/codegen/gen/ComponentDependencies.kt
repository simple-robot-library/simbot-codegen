package love.forte.simbot.codegen.gen

const val SIMBOT_GROUP = "love.forte.simbot"
const val SIMBOT_COMPONENT_GROUP = "love.forte.simbot.component"

// version

val SIMBOT_VERSION = GradleCatalogVersion(
    name = "simbot",
    version = "4.6.0" // copy to change
)

val KOTLIN_VERSION = GradleCatalogVersion(
    name = "kotlin",
    version = "2.0.0"
)

val SPRING_VERSION = GradleCatalogVersion(
    name = "spring",
    version = "3.3.3"
)

val SPRING_MANAGEMENT_VERSION = GradleCatalogVersion(
    name = "spring-management",
    version = "1.1.6"
)

val KTOR_VERSION = GradleCatalogVersion(
    name = "ktor",
    version = "2.3.12"
)

// deps

val SIMBOT_CORE = GradleCatalogVersionDependency(
    dependencyName = "simbot-core",
    group = SIMBOT_GROUP,
    name = "simbot-core",
    version = SIMBOT_VERSION,
)

val SIMBOT_SPRING = GradleCatalogVersionDependency(
    dependencyName = "simbot-spring",
    group = SIMBOT_GROUP,
    name = "simbot-core-spring-boot-starter",
    version = SIMBOT_VERSION,
)

// components

val COMPONENT_QQ = GradleCatalogVersionDependency(
    dependencyName = "simbot-component-qq",
    group = SIMBOT_COMPONENT_GROUP,
    name = "simbot-component-qq-guild-core",
    version = GradleCatalogVersion(
        name = "simbot-qq",
        version = "4.0.1"
    )
)

val COMPONENT_KOOK = GradleCatalogVersionDependency(
    dependencyName = "simbot-component-kook",
    group = SIMBOT_COMPONENT_GROUP,
    name = "simbot-component-kook-core",
    version = GradleCatalogVersion(
        name = "simbot-kook",
        version = "4.0.2"
    )
)

val COMPONENT_OB_11 = GradleCatalogVersionDependency(
    dependencyName = "simbot-component-onebot",
    group = SIMBOT_COMPONENT_GROUP,
    name = "simbot-component-onebot-v11-core",
    version = GradleCatalogVersion(
        name = "simbot-onebot",
        version = "1.4.0"
    )
)

// Spring

val SPRING_STARTER = GradleCatalogVersionDependency(
    dependencyName = "spring-boot-starter",
    group = "org.springframework.boot",
    name = "spring-boot-starter",
    version = null
)

// Ktor

const val KTOR_GROUP = "io.ktor"

val KTOR_CLIENT_CORE = GradleCatalogVersionDependency(
    dependencyName = "ktor-client-core",
    group = KTOR_GROUP,
    name = "ktor-client-core",
    version = KTOR_VERSION
)

val KTOR_CLIENT_JAVA = GradleCatalogVersionDependency(
    dependencyName = "ktor-client-java",
    group = KTOR_GROUP,
    name = "ktor-client-java",
    version = KTOR_VERSION
)

// val KTOR_CLIENT_CIO = GradleCatalogVersionDependency(
//     dependencyName = "ktor-client-cio",
//     group = KTOR_GROUP,
//     name = "ktor-client-cio",
//     version = KTOR_VERSION
// )

// Others

val KOTLIN_REFLECT = GradleCatalogVersionDependency(
    dependencyName = "kotlin-reflect",
    group = "org.jetbrains.kotlin",
    name = "kotlin-reflect",
    version = KOTLIN_VERSION
)

// plugins

val PLUGIN_KOTLIN = GradleCatalogPlugin(
    pluginName = "kotlin-jvm",
    id = "org.jetbrains.kotlin.jvm",
    version = KOTLIN_VERSION,
)

val PLUGIN_KOTLIN_SPRING = GradleCatalogPlugin(
    pluginName = "kotlin-plugin-spring",
    id = "org.jetbrains.kotlin.plugin.spring",
    version = KOTLIN_VERSION,
)

val PLUGIN_SPRING = GradleCatalogPlugin(
    pluginName = "spring",
    id = "org.springframework.boot",
    version = SPRING_VERSION,
)

val PLUGIN_SPRING_MANAGEMENT = GradleCatalogPlugin(
    pluginName = "spring-management",
    id = "io.spring.dependency-management",
    version = SPRING_MANAGEMENT_VERSION,
)
