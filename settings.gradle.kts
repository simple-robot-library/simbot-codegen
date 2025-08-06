rootProject.name = "simbot-codegen"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }

    versionCatalogs {
        // https://github.com/JetBrains/kotlin-wrappers?tab=readme-ov-file
        create("kotlinWrappers") {
            val wrappersVersion = "2025.8.4"
            from("org.jetbrains.kotlin-wrappers:kotlin-wrappers-catalog:$wrappersVersion")
        }
    }
}

include(":composeApp")
// include(":jszip-kotlin")
include(":file-saver-kotlin")
include(":common")
