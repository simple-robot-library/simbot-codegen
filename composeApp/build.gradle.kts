plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

repositories {
    mavenLocal {
        mavenContent {
            snapshotsOnly()
            includeGroup("love.forte.codegentle")
        }
    }
    google()
    mavenCentral()
//    maven {
//        url = rootDir.resolve("libs").toURI()
//        mavenContent {
//            includeGroup("com.squareup")
//        }
//    }
}

// 刷新snapshot
configurations.all {
    resolutionStrategy {
        cacheChangingModulesFor(1, TimeUnit.MINUTES)
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        //outputModuleName = "composeApp"
        browser()
        // browser {
        //     val projectDirPath = project.projectDir.path
        //     commonWebpackConfig {
        //         outputFileName = "composeApp.js"
        //         devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
        //             static = (static ?: mutableListOf()).apply {
        //                 // Serve sources to debug inside browser
        //                 add(projectDirPath)
        //             }
        //         }
        //     }
        // }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.jb.compose.runtime)
            implementation(libs.jb.compose.foundation)
            implementation(libs.jb.compose.material3)
            implementation(libs.jb.compose.ui)
            implementation(libs.jb.compose.material.icons.extended)
            // https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-images-resources.html
            implementation(libs.jb.compose.components.resources)
            implementation(libs.jb.compose.ui.tooling.preview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)
            // https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-viewmodel.html#adding-the-common-viewmodel-to-your-project
            // implementation(libs.androidx.lifecycle.viewmodel.compose)
            // implementation(libs.androidx.lifecycle.runtime)
            // implementation(libs.androidx.lifecycle)
            implementation(libs.codegentle.kotlin)
            implementation(libs.codegentle.java)
            implementation(libs.kotlin.coroutine)

            implementation(libs.kotlin.serialization.core)
            implementation(libs.kotlin.serialization.json)

            implementation(libs.highlights)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        wasmJsMain.dependencies {
            implementation(kotlinWrappers.jszip)
            implementation(project(":file-saver-kotlin"))
            //implementation("site.addzero:compose-native-component-glass:2025.12.22")
        }

        wasmJsTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

