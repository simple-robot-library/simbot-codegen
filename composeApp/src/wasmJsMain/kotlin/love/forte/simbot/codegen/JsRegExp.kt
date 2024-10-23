package love.forte.simbot.codegen

external interface RegExpMatch {
    val index: Int
    val input: String
    val length: Int
}

external class RegExp(pattern: String, flags: String? = definedExternally) : JsAny {
    fun test(str: String): Boolean
    fun exec(str: String): RegExpMatch?
    override fun toString(): String

    var lastIndex: Int

    val global: Boolean
    val ignoreCase: Boolean
    val multiline: Boolean
}

internal fun RegExp.reset() {
    lastIndex = 0
}
