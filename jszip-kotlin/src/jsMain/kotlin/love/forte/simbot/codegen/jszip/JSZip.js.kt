package love.forte.simbot.codegen.jszip

actual fun createJsZipFileGenerateOptions(type: String): JsZipFileGenerateOptions {
    val obj = js("{}").unsafeCast<JsZipFileGenerateOptions>()
    obj.type = type
    return obj
    return js("{ type }")
}