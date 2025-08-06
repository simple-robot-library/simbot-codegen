package love.forte.codegen.common

import kotlin.js.definedExternally

actual external interface RegExpMatch {
    actual val index: Int
    actual val input: String
    actual val length: Int
}

actual external class RegExp actual constructor(pattern: String, flags: String?) {
    constructor(pattern: String)
    
    actual fun test(str: String): Boolean
    actual fun exec(str: String): RegExpMatch?
    actual override fun toString(): String

    actual var lastIndex: Int

    actual val global: Boolean
    actual val ignoreCase: Boolean
    actual val multiline: Boolean
}