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
            // Add common dependencies if needed
        }
        
        jsMain.dependencies {
            // Add JS-specific dependencies if needed
        }
        
        wasmJsMain.dependencies {
            implementation(libs.kotlin.browser)
        }
    }
}