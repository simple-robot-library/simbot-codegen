package love.forte.simbot.codegen.filesaver

import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.File

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

external interface FileSaverSaveAsOptions : JsAny {
    var autoBom: Boolean
}

/*
import { saveAs } from 'file-saver';
FileSaver saveAs(Blob/File/Url, optional DOMString filename, optional Object { autoBom })
 */
