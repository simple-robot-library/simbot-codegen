plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    js {
        browser()
        binaries.library()
    }
    
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.library()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.coroutine)
            api(kotlinWrappers.browser)
        }
        
        jsMain.dependencies {
            implementation(npm("file-saver", "^2.0.4"))
        }
        
        wasmJsMain.dependencies {
            implementation(npm("file-saver", "^2.0.4"))
        }
    }
}