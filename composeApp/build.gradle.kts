import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

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
        browser {
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.materialIconsExtended)
            // https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-images-resources.html
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)
            // https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-viewmodel.html#adding-the-common-viewmodel-to-your-project
            // implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0-rc03")
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
        }

        wasmJsTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

