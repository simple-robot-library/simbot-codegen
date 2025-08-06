@file:OptIn(ExperimentalWasmJsInterop::class)

package love.forte.codegen.common

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.definedExternally

actual external interface RegExpMatch : JsAny {
    actual val index: Int
    actual val input: String
    actual val length: Int
}

actual external class RegExp actual constructor(pattern: String, flags: String?) : JsAny {
    constructor(pattern: String)
    
    actual fun test(str: String): Boolean
    actual fun exec(str: String): RegExpMatch?
    actual override fun toString(): String

    actual var lastIndex: Int

    actual val global: Boolean
    actual val ignoreCase: Boolean
    actual val multiline: Boolean
}