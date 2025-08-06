package love.forte.simbot.codegen.codegen.naming

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.PackageName
import love.forte.codegentle.common.naming.parseToPackageName
import love.forte.codegentle.common.naming.plus

// TODO
object SimbotNames {
    val simbotPkg = "love.forte.simbot".parseToPackageName()
    val simbotEventPkg = simbotPkg + "event"
    val simbotSpringPkg = simbotPkg + "spring"
    val simbotQuantcatCommonAnoPkg = simbotPkg + "quantcat" + "common" + "annotations"

    val listenerAno = ClassName(simbotQuantcatCommonAnoPkg, "Listener")
    val filterAno = ClassName(simbotQuantcatCommonAnoPkg, "Filter")
    val enableSimbotAno = ClassName(simbotSpringPkg, "EnableSimbot")
    val contentTrimAno = ClassName(simbotQuantcatCommonAnoPkg, "ContentTrim")
    val eventClassName = ClassName(simbotEventPkg, "Event")
    val msgEventClassName = ClassName(simbotEventPkg, "MessageEvent")
    val contactMsgEventClassName = ClassName(simbotEventPkg, "ContactMessageEvent")
}