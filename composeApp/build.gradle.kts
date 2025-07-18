import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

repositories {
    google()
    mavenCentral()
    // maven {
    //     // https://ktor.io/eap/
    //     url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
    // }
    maven {
        url = rootDir.resolve("libs").toURI()
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "composeApp"
        useEsModules()
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
            implementation(libs.kotlinpoet)
            implementation(libs.kotlin.coroutine)
            val ktor = "3.1.3"
            implementation("io.ktor:ktor-client-core:$ktor")
            implementation("io.ktor:ktor-client-content-negotiation:$ktor")
            implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor")
            implementation("io.ktor:ktor-client-core:$ktor")

            implementation(libs.kotlin.serialization.core)
            implementation(libs.kotlin.serialization.json)

        }

        wasmJsMain.dependencies {
            // https://stuk.github.io/jszip/
            implementation(npm("jszip", "^3.10.1"))
            // FileSaver
            // https://github.com/eligrey/FileSaver.js
            implementation(npm("file-saver", "^2.0.4"))
            // implementation("io.ktor:ktor-client-core:3.0.0-beta-2")
            // implementation("io.ktor:ktor-client-js:3.0.0-beta-2")

            // implementation(npm(File(projectDir, "simbot-codegen-code-generator")))

            // val dir = rootProject.project("code-generator").projectDir
            //     .resolve("build/dist/js/productionLibrary")

            // val dir = File(
            //     rootProject.project(":code-generator").projectDir,
            //     "build/dist/js/productionLibrary"
            // )

            // implementation(project(":code-generator"))
            // implementation(npm(dir))
        }
    }
}

