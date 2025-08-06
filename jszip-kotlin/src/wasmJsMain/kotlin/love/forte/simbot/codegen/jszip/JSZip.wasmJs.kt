package love.forte.simbot.codegen.jszip

@OptIn(ExperimentalWasmJsInterop::class)
actual fun createJsZipFileGenerateOptions(type: String): JsZipFileGenerateOptions {
    js("return { type }")
}