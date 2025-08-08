Project-specific development guidelines for simbot-codegen

Last verified: 2025-08-08 (local), Gradle 8.10.2, Kotlin 2.2.20-Beta1

Overview
- This repo is a Kotlin Multiplatform (KMP) workspace primarily targeting Web (JS + Wasm/JS) with Compose Multiplatform for the UI.
- Modules:
  - composeApp: Web UI and code generation front-end (Wasm/JS executable).
  - common: Shared code and small utilities (JS + Wasm/JS libraries).
  - file-saver-kotlin: Minimal wrapper around the NPM `file-saver` package for Web downloads (JS + Wasm/JS).
  - jszip-kotlin: Wrapper for `jszip` (optional; not included by default in settings.gradle.kts).
- Tooling and versions are pinned via gradle/libs.versions.toml; Kotlin wrappers catalog is pulled in settings.gradle.kts.

Build and configuration notes
- Gradle and JDK
  - Verified with Gradle wrapper 8.10.2 and Azul JDK 24.0.1. Prefer a modern JDK (21+) when possible.
  - Build uses type-safe accessors and version catalogs. Do not hardcode versions; add/adjust in gradle/libs.versions.toml.
- Repositories
  - Mirrors are configured (Tencent/Huawei) for performance. Keep these unless you have a strong reason to modify.
  - Snapshots repo is added only for the group `love.forte.codegentle` in the root build.gradle.kts. Retain this scoping.
  - Local maven repo is enabled (mavenLocal()) and a local libs repo is used in composeApp for com.squareup KotlinPoet artifacts under ./libs.
- Targets and outputs
  - composeApp: wasmJs { browser(); binaries.executable() } with dev server static dir pointed to projectDir (helps browser debugging) and output JS name set to composeApp.js.
  - common, file-saver-kotlin, jszip-kotlin: js { browser() } and wasmJs { browser() } with binaries.library(). In common we also enable nodejs() for js/wasm to support Node-based tests.
- Useful Gradle tasks
  - Run the web app (dev): `./gradlew :composeApp:wasmJsBrowserDevelopmentRun`
  - Assemble release bundles for libs: :<module>:jsJar, :<module>:wasmJsJar
  - List tasks per module: ./gradlew :<module>:tasks --all
- Update Lock files
  - Run: `gradle kotlinUpgradeYarnLock kotlinWasmUpgradeYarnLock -i`

Node, NPM, and Yarn lock specifics
- Kotlin/JS plugin manages Node/Yarn automatically (see tasks: kotlinNodeJsSetup, kotlinWasmNodeJsSetup).
- The Yarn lock is stored under kotlin-js-store. When Kotlin or wrappers versions move, you may see:
  - Error: "Lock file was changed. Run the `kotlinUpgradeYarnLock` task to actualize lock file"
  - Fix: ./gradlew kotlinUpgradeYarnLock
- You may see "Ignored scripts due to flag" during npm/yarn steps; this is expected with the Kotlin/JS toolchain.

Testing: how to enable and run
- Framework
  - Use kotlin.test (libs.kotlin.test from version catalog). Tests can be placed in commonTest or platform test source sets (jsTest, wasmJsTest) of a given module.
- Enabling tests in a module
  - Add to the module’s build.gradle.kts inside sourceSets: commonTest.dependencies { implementation(libs.kotlin.test) }
  - For Node-based test tasks (fast CI-friendly), ensure the target also declares nodejs(). Example (present in :common):
    js { browser(); nodejs() }
    wasmJs { browser(); nodejs() }
- Available verification tasks (module-specific)
  - :<module>:jsNodeTest — Run JS tests in Node.
  - :<module>:wasmJsNodeTest — Run Wasm/JS tests in Node.
  - :<module>:jsBrowserTest — Run JS tests in a browser via Karma/Webpack (requires a local browser/headless setup).
  - :<module>:wasmJsBrowserTest — Run Wasm/JS tests in a browser via Karma/Webpack.

Demonstrated test run (validated locally)
- Context
  - Module: :common
  - Source set: common/src/commonTest
  - Example test (kotlin.test):
    package love.forte.simbot.codegen

    import kotlin.test.Test
    import kotlin.test.assertEquals
    import kotlin.test.assertTrue

    class SanityTest {
        @Test fun addition() { assertEquals(4, 2 + 2) }
        @Test fun truth() { assertTrue(1 in 0..2) }
    }
- Commands executed and outcomes
  1) First run emitted a lock warning; we updated the yarn lock:
     ./gradlew kotlinUpgradeYarnLock
  2) JS tests in Node (success):
     ./gradlew :common:jsNodeTest
     -> BUILD SUCCESSFUL
  3) Wasm/JS tests in Node (success):
     ./gradlew :common:wasmJsNodeTest
     -> BUILD SUCCESSFUL
- The sample test file was created only to validate the process and has been removed afterward to keep the repo clean.

Guidelines for adding new tests
- Prefer placing cross-platform tests into commonTest using kotlin.test.
- If a module lacks kotlin.test, add it to commonTest (or jsTest/wasmJsTest as needed) in build.gradle.kts using libs.kotlin.test.
- If you need Node-based test execution for a module, add nodejs() to the module’s js and/or wasmJs targets.
- Typical flows:
  - All tests for a module: ./gradlew :<module>:allTests
  - Only JS/Node: ./gradlew :<module>:jsNodeTest
  - Only Wasm/Node: ./gradlew :<module>:wasmJsNodeTest
  - Browser-based JS/Wasm: use jsBrowserTest/wasmJsBrowserTest (ensure a browser/pre-requisites are available).

Code style and other dev notes
- Kotlin code style is set to official (gradle.properties: kotlin.code.style=official).
- Keep freeCompilerArgs configured in modules unless you know why you change them:
  - composeApp uses -Xcontext-parameters; common uses -Xexpect-actual-classes.
- composeApp webpack dev server statically serves projectDir to allow in-browser source debugging; maintain that when tweaking webpack config.
- Library dependencies:
  - composeApp uses KotlinPoet and CodeGentle. KotlinPoet artifacts are resolved via a local ./libs repo declared in composeApp/build.gradle.kts. Do not remove unless you fully switch to maven artifacts.
  - file-saver-kotlin wraps NPM `file-saver`; jszip-kotlin wraps NPM `jszip`. Ensure NPM deps stay compatible with Kotlin wrappers in libs.versions.toml.

Troubleshooting
- Lock file changed -> run: ./gradlew kotlinUpgradeYarnLock
- Node or Binaryen bootstrap errors -> run: ./gradlew kotlinNodeJsSetup kotlinWasmNodeJsSetup kotlinWasmBinaryenSetup
- If browser tests fail due to missing browser, prefer Node-based test tasks (:jsNodeTest/:wasmJsNodeTest) for quick feedback.

Release and publishing (internal)
- Snapshots under group love.forte.codegentle are consumed from Sonatype snapshots; treat them as evolving APIs.

Notes
- The validated commands and behavior above were verified on 2025-08-08. If toolchain versions change, re-run kotlinUpgradeYarnLock and prefer Node-based tests for the first smoke check.
