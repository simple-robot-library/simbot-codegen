package love.forte.simbot.codegen.filesaver

@OptIn(ExperimentalWasmJsInterop::class)
actual external interface FileSaverSaveAsOptions : JsAny {
    actual var autoBom: Boolean
}
