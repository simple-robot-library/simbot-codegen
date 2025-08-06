package love.forte.codegen.common

/**
 * Common interface for JavaScript RegExp match result.
 * Platform-specific implementations handle the differences between JS and WasmJS.
 */
expect interface RegExpMatch {
    val index: Int
    val input: String
    val length: Int
}

/**
 * Common interface for JavaScript RegExp functionality.
 * Platform-specific implementations handle the differences between JS and WasmJS.
 */
expect class RegExp(pattern: String, flags: String? = null) {
    fun test(str: String): Boolean
    fun exec(str: String): RegExpMatch?
    override fun toString(): String

    var lastIndex: Int

    val global: Boolean
    val ignoreCase: Boolean
    val multiline: Boolean
}

/**
 * Utility function to reset the RegExp lastIndex.
 */
fun RegExp.reset() {
    lastIndex = 0
}