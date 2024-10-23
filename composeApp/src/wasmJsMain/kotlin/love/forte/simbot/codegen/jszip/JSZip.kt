package love.forte.simbot.codegen.jszip

import love.forte.simbot.codegen.JsDate
import love.forte.simbot.codegen.RegExp
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.w3c.files.Blob
import kotlin.js.Promise

@JsModule("jszip")
external class JSZip : JsAny {

    fun file(name: String): ZipObject?
    fun file(name: RegExp): ZipObject?

    /**
     * @param data String/ArrayBuffer/Uint8Array/Buffer/Blob/Promise/Nodejs stream
     */
    fun file(name: String, data: String, options: JsZipFileOptions = definedExternally): ZipObject?
    fun file(name: String, data: ArrayBuffer, options: JsZipFileOptions = definedExternally): ZipObject?
    fun file(name: String, data: Uint8Array, options: JsZipFileOptions = definedExternally): ZipObject?
    fun file(name: String, data: Blob, options: JsZipFileOptions = definedExternally): ZipObject?
    fun file(name: String, data: Promise<*>, options: JsZipFileOptions = definedExternally): ZipObject?

    fun folder(name: String): JSZip
    fun folder(name: RegExp): JSZip

    fun forEach(callback: (relativePath: String, file: ZipObject) -> Unit)

    fun filter(predicate: (relativePath: String, file: ZipObject) -> Unit)

    fun remove(name: String)


    /**
     * ```js
     * zip.generateAsync({type:"blob"})
     * .then(function (content) {
     *     // see FileSaver.js
     *     saveAs(content, "hello.zip");
     * });
     * ```
     *
     * ```js
     * zip.generateAsync({type:"base64"})
     * .then(function (content) {
     *     location.href="data:application/zip;base64,"+content;
     * });
     * ```
     *
     * ```js
     *zip.folder("folder_1").folder("folder_2").file("hello.txt", "hello");
     * // zip now contains:
     * // folder_1/
     * // folder_1/folder_2/
     * // folder_1/folder_2/hello.txt
     *
     * zip.folder("folder_1").generateAsync({type:"nodebuffer"})
     * .then(function (content) {
     *     // relative to folder_1/, this file only contains:
     *     // folder_2/
     *     // folder_2/hello.txt
     *     require("fs").writeFile("hello.zip", content, function(err){/*...*/});
     * });
     * ```
     */
    fun generateAsync(
        options: JsZipFileGenerateOptions,
        onUpdate: (percent: Double, currentFile: String) -> Unit = definedExternally
    ): Promise<JsAny>

    companion object {
        val version: String
    }

}

external interface JsZipFileOptions : JsAny {
    var base64: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var binary: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var date: JsDate?
        get() = definedExternally
        set(value) = definedExternally
    var compression: String?
        get() = definedExternally
        set(value) = definedExternally
    var compressionOptions: JsAny?
        get() = definedExternally
        set(value) = definedExternally
    var comment: String?
        get() = definedExternally
        set(value) = definedExternally
    var optimizedBinaryString: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var createFolders: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var unixPermissions: Int?
        get() = definedExternally
        set(value) = definedExternally
    var dosPermissions: Int?
        get() = definedExternally
        set(value) = definedExternally
    var dir: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface JsZipFileGenerateOptions : JsAny {
    /**
     * - `blob`
     * - `base64`
     */
    var type: String?
        get() = definedExternally
        set(value) = definedExternally
    var compression: String?
        get() = definedExternally
        set(value) = definedExternally
    var compressionOptions: JsAny?
        get() = definedExternally
        set(value) = definedExternally
    var comment: String?
        get() = definedExternally
        set(value) = definedExternally
    var mimeType: String?
        get() = definedExternally
        set(value) = definedExternally
    var platform: String?
        get() = definedExternally
        set(value) = definedExternally
    var encodeFileName: JsAny?
        get() = definedExternally
        set(value) = definedExternally
    var streamFiles: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

@JsModule("jszip")
external class ZipObject : JsAny {
    val name: String
    val dir: Boolean
    val date: JsDate
    val comment: String?
    val unixPermissions: Int
    val dosPermissions: Int
    val options: JsAny?
}

fun JsZipFileGenerateOptions(
    type: String,
): JsZipFileGenerateOptions {
    js("return { type }")
}
