@file:OptIn(ExperimentalWasmJsInterop::class)

package love.forte.simbot.codegen.filesaver

import web.blob.Blob
import web.file.File
import web.url.URL
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsModule
import kotlin.js.definedExternally

// External declarations for file-saver library - simplified for wasmJs
@JsModule("file-saver")
external fun saveAs(
    file: File,
    filename: String = definedExternally,
    options: FileSaverSaveAsOptions = definedExternally
)

@JsModule("file-saver")
external fun saveAs(
    file: Blob,
    filename: String = definedExternally,
    options: FileSaverSaveAsOptions = definedExternally
)

@JsModule("file-saver")
external fun saveAs(
    file: URL,
    filename: String = definedExternally,
    options: FileSaverSaveAsOptions = definedExternally
)

@JsModule("file-saver")
external fun saveTextAs(
    content: String,
    fileName: String = definedExternally,
    charset: String = definedExternally
)

expect interface FileSaverSaveAsOptions {
    var autoBom: Boolean
}
