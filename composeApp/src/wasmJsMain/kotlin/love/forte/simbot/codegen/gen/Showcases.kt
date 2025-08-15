package love.forte.simbot.codegen.gen

import jszip.JSZip
import love.forte.codegentle.common.code.*
import love.forte.codegentle.common.naming.ArrayTypeName
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.MemberName
import love.forte.codegentle.common.naming.parseToPackageName
import love.forte.codegentle.common.ref.addAnnotation
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.kotlin.*
import love.forte.codegentle.kotlin.naming.KotlinClassNames
import love.forte.codegentle.kotlin.spec.KotlinFunctionSpec
import love.forte.codegentle.kotlin.spec.addFunction
import love.forte.codegentle.kotlin.spec.addMainFunction
import love.forte.codegentle.kotlin.spec.addParameter
import love.forte.simbot.codegen.codegen.SimbotComponent
import love.forte.simbot.codegen.codegen.naming.SimbotNames
import love.forte.simbot.codegen.codegen.naming.SpringNames
import love.forte.simbot.codegen.codegen.SimbotComponent.*
import love.forte.simbot.codegen.toRelativePath0


/**
 * 生成使用Spring时的示例们到 sourceSets 中。
 */
fun emitSpringShowcases(
    projectPackage: String,
    components: Collection<SimbotComponent>,
    sourceSets: JSZip,
    resources: JSZip
) {
    // 主要的Application
    emitSpringMainFile(projectPackage, sourceSets)

    // Listeners
    emitSpringListenerShowcases(projectPackage, components, sourceSets)

    // 配置文件
    if (components.isNotEmpty()) {
        val botsFolder = resources.folder("simbot-bots")!!
        components.forEach {
            val configJson = genComponentConfigShowcases(it)
            botsFolder.file("${it.simpleId}-example.bot.json", configJson)
        }
    }
    resources.file("application.yml", genSpringApplicationConfig())
}

fun genKotlinSpringMainFile(
    name: String,
    projectPackage: String
): KotlinFile {
    val mainFile = KotlinFile(projectPackage.parseToPackageName()) {
        addSimpleClassType(name) {
            addAnnotation(SimbotNames.enableSimbotAno)
            addAnnotation(SpringNames.springBootApplicationAno)
            addDoc("Spring程序的入口注解类。添加 [%V] 注解来标记启用 simbot 相关的功能。") {
                emitType(SimbotNames.enableSimbotAno)
            }
        }

        addMainFunction {
            addParameter("args", ArrayTypeName(KotlinClassNames.STRING.ref()))
            // val ram = MemberName("org.springframework.boot", "runApplication")
            // addStatement("%M<%L>(*args)", ram, name)
            addStatement("%V<%V>(*args)") {
                emitType(MemberName("org.springframework.boot", "runApplication"))
                emitLiteral(name)
            }
        }
    }

    return mainFile
}

fun emitSpringMainFile(
    projectPackage: String,
    sourceDir: JSZip
) {
    val file = genKotlinSpringMainFile("MainApplication", projectPackage)
    sourceDir.file(file.toRelativePath0(), file.writeToKotlinString())
}

fun genKotlinSpringListenerShowcases(
    projectPackage: String,
    components: Collection<SimbotComponent>
): KotlinFile {
    var showcaseCount = 1
    val handlePackage = "$projectPackage.handle"

    val myHandleFile = KotlinFile(handlePackage.parseToPackageName()) {
        // Text + Message
        addStaticImport("love.forte.simbot.message.plus")

        addSimpleClassType("MyEventHandles") {
            addAnnotation(SpringNames.componentAno)
            addDoc("一个用于承载监听函数的事件处理器类。\n\n")
            addDoc("将它标记为 [%V] 以交由Spring进行管理，\n") {
                emitType(SpringNames.componentAno)
            }
            addDoc("simbot-spring 会解析其中标记了 [%V] 的函数们。") {
                emitType(SimbotNames.listenerAno)
            }

            // 1, 监听所有事件，然后控制台输出
            addFunction("handleAllAndPrint") {
                addAnnotation(SimbotNames.listenerAno)
                addDoc("示例${showcaseCount++}: 监听所有的事件，然后将它们输出到控制台。\n\n")
                addDoc("@param event 你要监听的事件的类型。\n")
                addDoc("必须是一个 [%V] 类型的子类", CodePart.type(SimbotNames.eventClassName))

                addParameter("event", SimbotNames.eventClassName)
                addStatement("println(%V)", CodePart.string($$"收到事件: $event", false))
            }

            // 2, 过滤消息事件
            addFunction("handleMessageEvent") {
                addAnnotation(SimbotNames.listenerAno)
                addAnnotation(SimbotNames.contentTrimAno)
                addAnnotation(SimbotNames.filterAno) {
                    addMember("", "%V", CodePart.string("你好.*"))
                }
                modifiers.suspend()
                addDoc("示例${showcaseCount++}: 监听所有 **文本内容** 开头为 `\"你好\"` 的消息事件，\n")
                addDoc("然后在控制台输出它的消息链内容。\n\n")
                addDoc("这里通过 [%V] 来以注解的风格便捷的匹配消息内容并过滤, \n", CodePart.type(SimbotNames.filterAno))
                addDoc(
                    "并配合使用 [%V] 在匹配前优先处理掉匹配文本的前后空字符，避免匹配失效。\n",
                    CodePart.type(SimbotNames.contentTrimAno)
                )

                addParameter("event", SimbotNames.msgEventClassName)
                addStatement("println(%V)", CodePart.string("收到消息事件: \$event", false))
                addStatement("")

                addCode {
                    beginControlFlow("for ((index, element) in event.messageContent.messages.withIndex())")
                    addStatement("println(%V)", CodePart.string($$"\\t消息元素[$index]: $element", false))
                }
            }

            // 3, 组件专属
            // 挑其中一个组件
            val oneOfComponent = components.firstOrNull()
            if (oneOfComponent != null) {
                addFunction(componentShowcase(showcaseCount++, oneOfComponent))
            }

            // 4, 回复消息
            addFunction("handleAndReply") {
                addAnnotation(SimbotNames.listenerAno)
                modifiers.suspend()

                addDoc("示例${showcaseCount++}: 监听所有 **文本内容** 开头为 `\"你好\"` 的消息事件，\n")

                addParameter("event", SimbotNames.contactMsgEventClassName)
                addComment("基于事件回复一句\"你好\"")
                addStatement("event.reply(%V)", CodePart.string("你好"))
                addStatement("")
                addComment("或直接根据 content 发送一句\"你又好\"")
                addStatement("event.content().send(%V)", CodePart.string("你又好"))
                addStatement("")
                addComment("可以发送文本(字符串)、消息元素/消息链和事件中的消息正文。")
                addStatement("")

                addComment("下面的示例是发送一个消息链，其中包括一个文字消息和一个图片, ")
                addComment("它们二者直接使用 + 拼接。")
                addComment("你也可以使用 buildMessages { ... } 来构建消息链。")
                addComment("更多有关消息链和可用的消息元素，请参考文档：")
                addComment("https://simbot.forte.love/basic-messages.html")
                addStatement(
                    "val messages = %V { %V } + " +
                            "%V(%V).%V()"
                ) {
                    emitType(MemberName("love.forte.simbot.message", "Text"))
                    emitString("图片: ")
                    emitType(MemberName("kotlin.io.path", "Path"))
                    emitString("image.png")
                    emitType(MemberName("love.forte.simbot.message.OfflinePathImage.Companion", "toOfflineImage"))
                }
                addStatement("event.reply(messages)")
            }
        }
    }

    return myHandleFile
}

fun componentShowcase(
    index: Int,
    component: SimbotComponent
): KotlinFunctionSpec {
    return KotlinFunctionSpec("handleComponentSpecialEvent") {
        addAnnotation(SimbotNames.listenerAno)
        modifiers.suspend()
        val specialEvent = when (component) {
            QQ -> ClassName(
                "love.forte.simbot.component.qguild.event",
                "QGMessageEvent"
            )

            KOOK -> ClassName(
                "love.forte.simbot.component.kook.event",
                "KookMessageEvent"
            )

            OB -> ClassName(
                "love.forte.simbot.component.onebot.v11.core.event.message",
                "OneBotMessageEvent"
            )
        }

        addDoc("示例$index: 监听一个以${component.display}组件为例的特殊事件: [%V]。", CodePart.type(specialEvent))
        addDoc("它是在此组件下专属的消息事件类型。\n\n每个组件都有很多专属于自己组件内的事件类型，\n")
        addDoc("如果你想要监听一些更加具体的事件，不妨使用它们。")

        addParameter("event", specialEvent)
        addStatement("println(%V)", CodePart.string($$"收到$${component.display}组件消息事件: $event", false))
    }

    // return FunSpec.builder("handleComponentSpecialEvent").apply {
    //     addAnnotation(listenerAno)
    //     addModifiers(KModifier.SUSPEND)
    //     val specialEvent = when (component) {
    //         QQ -> ClassName("love.forte.simbot.component.qguild.event", "QGMessageEvent")
    //         KOOK -> ClassName("love.forte.simbot.component.kook.event", "KookMessageEvent")
    //         OB -> ClassName(
    //             "love.forte.simbot.component.onebot.v11.core.event.message",
    //             "OneBotMessageEvent"
    //         )
    //     }
    //     addKdoc("示例$index: 监听一个以${component.display}组件为例的特殊事件:·[%T]·。", specialEvent)
    //     addKdoc("它是在此组件下专属的消息事件类型。\n\n每个组件都有很多专属于自己组件内的事件类型，\n")
    //     addKdoc("如果你想要监听一些更加具体的事件，不妨使用它们。")
    //
    //     addParameter("event", specialEvent)
    //     addStatement("println(%P)", "收到${component.display}组件消息事件: \$event")
    // }.build()
}


fun emitSpringListenerShowcases(
    projectPackage: String,
    components: Collection<SimbotComponent>,
    sourceDir: JSZip
) {
    val file = genKotlinSpringListenerShowcases(projectPackage, components)
    sourceDir.file(file.toRelativePath0(), file.writeToKotlinString())
}

/**
 * 为Spring情况下生成组件的配置文件示例
 */
fun genComponentConfigShowcases(
    component: SimbotComponent,
): String {
    return when (component) {
        // language=JSON
        QQ -> """
            |{
            |    "component": "simbot.qqguild",
            |    "ticket": {
            |        "appId": "你的botId",
            |        "secret": "你的bot secret, 用于获取API的access_token (4.0.0-beta6开始)",
            |        "token": "你的bot token, 从 4.0.0-beta6 之后暂时不再会用到了"
            |    },
            |    "config": {
            |        "serverUrl": "SANDBOX",
            |        "disableWs": true
            |    }
            |}
        """.trimMargin()
        // language=JSON
        KOOK -> """
            |{
            |    "component": "simbot.kook",
            |    "ticket": {
            |        "clientId": "你的bot的client id",
            |        "token": "你的bot的ws token"
            |    }
            |}
        """.trimMargin()
        // language=JSON
        OB -> """
            |{
            |    "component": "simbot.onebot11",
            |    "authorization": {
            |        "botUniqueId": "你bot的唯一id，建议是bot的qq号，例如: 12345678",
            |        "apiServerHost": "http://localhost:3000",
            |        "eventServerHost":"ws://localhost:3001"
            |    }
            |}
        """.trimMargin()
    }
}

/**
 * 生成 Spring 的 `application.yml` 内容
 */
fun genSpringApplicationConfig(): String {
    return ""
}

