@file:OptIn(ExperimentalWasmJsInterop::class)

package love.forte.codegen.common

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny

@JsName("Date")
actual external class JsDate actual constructor() : JsAny {
    actual constructor(dateString: String)
    actual constructor(year: Int, month: Int)
    actual constructor(year: Int, month: Int, day: Int)
    actual constructor(year: Int, month: Int, day: Int, hour: Int)
    actual constructor(year: Int, month: Int, day: Int, hour: Int, minute: Int)
    actual constructor(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int)

    actual fun getDate(): Int
    actual fun getDay(): Int
    actual fun getFullYear(): Int
    actual fun getHours(): Int
    actual fun getMilliseconds(): Int
    actual fun getMinutes(): Int
    actual fun getMonth(): Int
    actual fun getSeconds(): Int
    actual fun getTime(): Double
    actual fun getTimezoneOffset(): Int
    actual fun getUTCDate(): Int
    actual fun getUTCDay(): Int
    actual fun getUTCFullYear(): Int
    actual fun getUTCHours(): Int
    actual fun getUTCMilliseconds(): Int
    actual fun getUTCMinutes(): Int
    actual fun getUTCMonth(): Int
    actual fun getUTCSeconds(): Int
    actual fun toDateString(): String
    actual fun toISOString(): String
    actual fun toTimeString(): String
    actual fun toUTCString(): String

    actual companion object {
        actual fun now(): Double
        actual fun parse(dateString: String): Double
    }
}