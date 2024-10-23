package love.forte.simbot.codegen.gen

import love.forte.simbot.codegen.gen.SimbotComponent.KOOK
import love.forte.simbot.codegen.gen.SimbotComponent.OB
import love.forte.simbot.codegen.gen.SimbotComponent.QQ
import com.squareup.kotlinpoet.ARRAY
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STRING
import love.forte.simbot.codegen.jszip.JSZip


/**
 * 生成使用核心库时的示例们到 sourceSets 中。
 */
fun genCoreSourceShowcases(
    projectName: String,
    projectPackage: String,
    sourceSets: JSZip,
    resources: JSZip
) {
    // Main, 构建App、加载组件
    TODO()

}

private val componentAno = ClassName("org.springframework.stereotype", "Component")
private val listenerAno = ClassName("love.forte.simbot.quantcat.common.annotations", "Listener")
private val filterAno = ClassName("love.forte.simbot.quantcat.common.annotations", "Filter")
private val contentTrimAno = ClassName("love.forte.simbot.quantcat.common.annotations", "ContentTrim")
private val eventClassName = ClassName("love.forte.simbot.event", "Event")
private val msgEventClassName = ClassName("love.forte.simbot.event", "MessageEvent")
private val contactMsgEventClassName = ClassName("love.forte.simbot.event", "ContactMessageEvent")


/**
 * 生成使用Spring时的示例们到 sourceSets 中。
 */
fun emitSpringShowcases(
    projectName: String,
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
        val botsFolder = resources.folder("simbot-bots")
        components.forEach {
            val configJson = genComponentConfigShowcases(it)
            botsFolder.file("${it.simpleId}-example.bot.json", configJson)
        }
    }
    resources.file("application.yml", genSpringApplicationConfig())
}

fun genSpringMainFile(
    projectPackage: String
): FileSpec {
    val name = "MainApplication"

    val mainFile = fileSpec(projectPackage, "MainApplication") {
        // Main Application class
        addClass(name) {
            val simbotAno = ClassName("love.forte.simbot.spring", "EnableSimbot")
            addAnnotation(simbotAno)
            addAnnotation(ClassName("org.springframework.boot.autoconfigure", "SpringBootApplication"))
            addKdoc("Spring程序的入口注解类。添加 [%T] 注解来标记启用 simbot 相关的功能。", simbotAno)
        }

        // Main function
        addFunction("main") {
            addParameter("args", ARRAY.parameterizedBy(STRING))
            val ram = MemberName("org.springframework.boot", "runApplication")
            addStatement("%M<%L>(*args)", ram, name)
        }

    }

    return mainFile
}

fun emitSpringMainFile(
    projectPackage: String,
    sourceSets: JSZip
) {
    val file = genSpringMainFile(projectPackage)
    sourceSets.file(file.relativePath, file.toString().removeAllPublicModifier())
}

fun genSpringListenerShowcases(
    projectPackage: String,
    components: Collection<SimbotComponent>
): FileSpec {
    var showcaseCount = 1
    val handlePackage = "$projectPackage.handle"

    val myHandleFile = fileSpec(handlePackage, "MyEventHandles") {
        addClass("MyEventHandles") {
            addAnnotation(componentAno)
            addKdoc("一个用于承载监听函数的事件处理器类。\n\n")
            addKdoc("将它标记为 [%T] 以交由Spring进行管理，\n", componentAno)
            addKdoc("simbot-spring 会解析其中标记了 [%T] 的函数们。", listenerAno)

            // 1, 监听所有事件，然后控制台输出
            addFunction("handleAllAndPrint") {
                addAnnotation(listenerAno)
                addModifiers(KModifier.SUSPEND)
                addKdoc("示例${showcaseCount++}: 监听所有的事件，然后将它们输出到控制台。\n\n")
                addKdoc("@param event 你要监听的事件的类型。\n")
                addKdoc("必须是一个 [%T] 类型的子类", eventClassName)

                addParameter("event", eventClassName)
                addStatement("println(%P)", "收到事件: \$event")
            }

            // 2, 过滤消息事件
            addFunction("handleMessageEvent") {
                addAnnotation(listenerAno)
                addAnnotation(AnnotationSpec.builder(filterAno).apply {
                    addMember("%S", "你好.*")
                }.build())
                addAnnotation(contentTrimAno)
                addModifiers(KModifier.SUSPEND)
                addKdoc("示例${showcaseCount++}: 监听所有 **文本内容** 开头为 `\"你好\"` 的消息事件，\n")
                addKdoc("然后在控制台输出它的消息链内容。\n\n")
                addKdoc("这里通过 [%T] 来以注解的风格便捷的匹配消息内容并过滤, \n", filterAno)
                addKdoc("并配合使用 [%T] 在匹配前优先处理掉匹配文本的前后空字符，避免匹配失效。\n", contentTrimAno)

                addParameter("event", msgEventClassName)
                addStatement("println(%P)", "收到消息事件: \$event")
                addStatement("")

                addCode(CodeBlock.builder().apply {
                    inControlFlow("for·((index,·element)·in·event.messageContent.messages.withIndex())") {
                        addStatement("println(%P)", "\\t消息元素[\$index]: \$element")
                    }
                }.build())
            }

            // 3, 组件专属
            // 挑其中一个组件
            val oneOfComponent = components.firstOrNull()
            if (oneOfComponent != null) {
                addFunction(componentShowcase(showcaseCount++, oneOfComponent))
            }

            // 4, 回复消息
            addFunction("handleAndReply") {
                addAnnotation(listenerAno)
                addModifiers(KModifier.SUSPEND)

                addKdoc("示例${showcaseCount++}: 监听所有 **文本内容** 开头为 `\"你好\"` 的消息事件，\n")

                addParameter("event", contactMsgEventClassName)
                addComment("基于事件回复一句\"你好\"")
                addStatement("event.reply(%S)", "你好")
                addStatement("")
                addComment("或直接根据 content 发送一句\"你好\"")
                addStatement("event.content().send(%S)", "你好")
                addStatement("")
                addComment("可以发送文本(字符串)、消息元素/消息链和事件中的消息正文。")
                addStatement("")

                addComment("下面的示例是发送一个消息链，其中包括一个文字消息和一个图片, ")
                addComment("它们二者直接使用 + 拼接。")
                addComment("你也可以使用 buildMessages { ... } 来构建消息链。")
                addComment("更多有关消息链和可用的消息元素，请参考文档：")
                addComment("https://simbot.forte.love/basic-messages.html")
                addStatement(
                    "val messages = %M { %S } + " +
                            "%M(%S).%M()",
                    MemberName("love.forte.simbot.message", "Text"),
                    "图片: ",
                    MemberName("kotlin.io.path", "Path"),
                    "image.png",
                    MemberName("love.forte.simbot.message.OfflinePathImage.Companion", "toOfflineImage")
                )
                addStatement("event.reply(messages)")
            }

            // Text + Image 消息拼接的操作符
            addImport("love.forte.simbot.message", "plus")
        }
    }

    return myHandleFile
}

fun componentShowcase(
    index: Int,
    component: SimbotComponent
): FunSpec {
    return FunSpec.builder("handleComponentSpecialEvent").apply {
        addAnnotation(listenerAno)
        addModifiers(KModifier.SUSPEND)
        val specialEvent = when (component) {
            QQ -> ClassName("love.forte.simbot.component.qguild.event", "QGMessageEvent")
            KOOK -> ClassName("love.forte.simbot.component.kook.event", "KookMessageEvent")
            OB -> ClassName(
                "love.forte.simbot.component.onebot.v11.core.event.message",
                "OneBotMessageEvent"
            )
        }
        addKdoc("示例$index: 监听一个以${component.display}组件为例的特殊事件:·[%T]·。", specialEvent)
        addKdoc("它是在此组件下专属的消息事件类型。\n\n每个组件都有很多专属于自己组件内的事件类型，\n")
        addKdoc("如果你想要监听一些更加具体的事件，不妨使用它们。")

        addParameter("event", specialEvent)
        addStatement("println(%P)", "收到${component.display}组件消息事件: \$event")
    }.build()
}


fun emitSpringListenerShowcases(
    projectPackage: String,
    components: Collection<SimbotComponent>,
    sourceSets: JSZip
) {
    val file = genSpringListenerShowcases(projectPackage, components)
    sourceSets.file(file.relativePath, file.toString().removeAllPublicModifier())
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
            |        "serverUrl": "SANDBOX"
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

