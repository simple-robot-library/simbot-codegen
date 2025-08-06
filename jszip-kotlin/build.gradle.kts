plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    js {
        browser()
        nodejs()
        binaries.library()
    }
    
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
        binaries.library()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.coroutine)
            api(libs.kotlin.browser)
        }
        
        jsMain.dependencies {
            implementation(npm("jszip", "^3.10.1"))
        }
        
        wasmJsMain.dependencies {
            implementation(npm("jszip", "^3.10.1"))
        }
    }
}