package love.forte.simbot.codegen

@JsName("Date")
external class JsDate() {
    constructor(dateString: String)

    constructor(year: Int, month: Int)

    constructor(year: Int, month: Int, day: Int)

    constructor(year: Int, month: Int, day: Int, hour: Int)

    constructor(year: Int, month: Int, day: Int, hour: Int, minute: Int)

    constructor(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int)

    fun getDate(): Int

    fun getDay(): Int

    fun getFullYear(): Int

    fun getHours(): Int

    fun getMilliseconds(): Int

    fun getMinutes(): Int

    fun getMonth(): Int

    fun getSeconds(): Int

    fun getTime(): Double

    fun getTimezoneOffset(): Int

    fun getUTCDate(): Int

    fun getUTCDay(): Int

    fun getUTCFullYear(): Int

    fun getUTCHours(): Int

    fun getUTCMilliseconds(): Int

    fun getUTCMinutes(): Int

    fun getUTCMonth(): Int

    fun getUTCSeconds(): Int

    fun toDateString(): String

    fun toISOString(): String

    fun toTimeString(): String

    fun toUTCString(): String

    companion object {
        fun now(): Double
        fun parse(dateString: String): Double
    }
}
