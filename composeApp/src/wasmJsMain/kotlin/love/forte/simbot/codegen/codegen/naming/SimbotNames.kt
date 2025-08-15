package love.forte.simbot.codegen.codegen.naming

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.parseToPackageName

// TODO

@Suppress("unused")
object SimbotNames {
    val simbotPkg = "love.forte.simbot".parseToPackageName()
    val simbotEventPkg = "love.forte.simbot.event".parseToPackageName()
    val simbotSpringPkg = "love.forte.simbot.spring".parseToPackageName()
    val simbotQuantcatCommonAnoPkg = "love.forte.simbot.quantcat.common.annotations".parseToPackageName()
    val simbotApplicationPkg = "love.forte.simbot.application".parseToPackageName()

    val listenerAno = ClassName(simbotQuantcatCommonAnoPkg, "Listener")
    val filterAno = ClassName(simbotQuantcatCommonAnoPkg, "Filter")
    val enableSimbotAno = ClassName(simbotSpringPkg, "EnableSimbot")
    val contentTrimAno = ClassName(simbotQuantcatCommonAnoPkg, "ContentTrim")
    val eventClassName = ClassName(simbotEventPkg, "Event")
    val msgEventClassName = ClassName(simbotEventPkg, "MessageEvent")
    val contactMsgEventClassName = ClassName(simbotEventPkg, "ContactMessageEvent")

    // Application classes for Core framework
    val application = ClassName(simbotApplicationPkg, "Application")
    val applicationBuilder = ClassName(simbotApplicationPkg, "ApplicationBuilder")
}
