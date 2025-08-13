package love.forte.simbot.codegen.gen.core.generators.java

import love.forte.codegentle.common.code.*
import love.forte.codegentle.common.naming.*
import love.forte.codegentle.common.ref.addAnnotation
import love.forte.codegentle.common.ref.annotationRef
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.java.JavaFile
import love.forte.codegentle.java.JavaModifier
import love.forte.codegentle.java.modifiers
import love.forte.codegentle.java.naming.JavaClassNames
import love.forte.codegentle.java.naming.JavaPrimitiveTypeNames
import love.forte.codegentle.java.spec.*
import love.forte.simbot.codegen.codegen.naming.SimbotNames
import love.forte.simbot.codegen.codegen.naming.SpringNames
import love.forte.simbot.codegen.gen.core.Component
import love.forte.simbot.codegen.gen.core.JavaStyle
import love.forte.simbot.codegen.gen.core.generators.java.JavaTemplates.DEFAULT_MAIN_CLASS_NAME

/**
 * Java 代码模板常量和工具类。
 *
 * 提供所有 Java 代码生成中使用的模板、常量和工具方法，
 * 确保代码的高复用性和可维护性。
 *
 * @author ForteScarlet
 */
object JavaTemplates {

    // ==================== 常量定义 ====================

    /** 默认主应用类名 */
    const val DEFAULT_MAIN_CLASS_NAME = "MainApplication"

    /** 默认事件处理器类名 */
    const val DEFAULT_EVENT_HANDLER_CLASS_NAME = "EventHandlers"

    /** 处理器包名后缀 */
    const val HANDLER_PACKAGE_SUFFIX = "handle"

    /** 默认过滤器文本 */
    const val DEFAULT_FILTER_TEXT = "你好.*"

    /** 默认回复消息1 */
    const val DEFAULT_REPLY_MESSAGE_1 = "你好"

    /** 默认回复消息2 */
    const val DEFAULT_REPLY_MESSAGE_2 = "你又好"

    // ==================== Spring 框架模板 ====================

    /**
     * 生成 Spring Boot 主应用类文件。
     *
     * @param packageName 包名
     * @param className 类名
     * @return 生成的 JavaFile 对象
     */
    fun createSpringMainClassFile(packageName: String, className: String): JavaFile {
        val packageNameParsed = packageName.parseToPackageName()

        val mainClass = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, className) {
            addModifiers(JavaModifier.PUBLIC)

            addDoc("Spring程序的入口注解类。\n添加 {@link %V} 注解来标记启用 simbot 相关的功能。") {
                emitType(SimbotNames.enableSimbotAno)
            }

            // Add annotations
            addAnnotation(SpringNames.springBootApplicationAno)
            addAnnotation(SimbotNames.enableSimbotAno)

            addMainMethod {
                modifiers { public(); static() }
                returns(JavaPrimitiveTypeNames.VOID.ref())
                addParameter(
                    JavaParameterSpec(
                        ArrayTypeName(JavaClassNames.STRING.ref()).ref(),
                        "args"
                    )
                )

                addStatement("%V.run(%V.class, args)") {
                    emitType(ClassName("org.springframework.boot", "SpringApplication"))
                    emitType(ClassName(packageName, className))
                }
            }
        }

        return JavaFile(packageNameParsed, mainClass)
    }

    /**
     * Spring Boot 主应用类模板 - 保持向后兼容。
     *
     * @param packageName 包名
     * @param className 类名
     * @return 生成的 Spring Boot 主应用类代码字符串
     */
    fun springMainClassTemplate(packageName: String, className: String): String {
        val file = createSpringMainClassFile(packageName, className)
        return file.toString()
    }

    // ==================== Core 框架模板 ====================

    /**
     * 生成核心库主应用类文件。
     *
     * @param packageName 包名
     * @param className 类名
     * @param javaStyle Java 编程风格
     * @return 生成的 JavaFile 对象
     */
    fun createCoreMainClassFile(packageName: String, className: String, javaStyle: JavaStyle): JavaFile {
        val packageNameParsed = packageName.parseToPackageName()

        val mainClass = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, className) {
            addModifiers(JavaModifier.PUBLIC)

            addDoc("核心库程序的入口类。")

            addMainMethod {
                modifiers { public(); static() }
                if (javaStyle == JavaStyle.ASYNC) {
                    returns(ClassName("java.util.concurrent", "CompletableFuture").ref())
                } else {
                    returns(JavaPrimitiveTypeNames.VOID.ref())
                    addException(ClassName("java.lang", "Exception").ref())
                }
                addParameter(
                    JavaParameterSpec(
                        ArrayTypeName(JavaClassNames.STRING.ref()).ref(),
                        "args"
                    )
                )

                if (javaStyle == JavaStyle.ASYNC) {
                    addStatement(
                        """
                            return %V.create()
                                .buildAsync()
                                .thenCompose(%V::joinAsync)
                                """.trimIndent()
                    ) {
                        emitType(SimbotNames.applicationBuilder)
                        emitType(SimbotNames.application)
                    }
                } else {
                    addStatement("%V application = %V.create().build()") {
                        emitType(SimbotNames.application)
                        emitType(SimbotNames.applicationBuilder)
                    }
                    addStatement("application.join()")
                }
            }
        }

        return JavaFile(packageNameParsed, mainClass)
    }

    // ==================== 完整事件处理器类生成 (codegentle-java) ====================

    /**
     * 生成完整的 Spring 事件处理器类文件。
     *
     * @param packageName 基础包名
     * @param components 组件列表
     * @param javaStyle Java 编程风格
     * @return JavaFile 对象
     */
    fun createSpringEventHandlerFile(packageName: String, components: List<Component>, javaStyle: JavaStyle): JavaFile {
        val handlerPackage = "$packageName.$HANDLER_PACKAGE_SUFFIX".parseToPackageName()

        val handlerClass = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, DEFAULT_EVENT_HANDLER_CLASS_NAME) {
            addModifiers(JavaModifier.PUBLIC)

            addDoc("一个用于承载监听函数的事件处理器类。\n\n")
            addDoc("将它标记为 {@link %V} 以交由Spring进行管理，\n") {
                emitType(SpringNames.componentAno)
            }
            addDoc("simbot-spring 会解析其中标记了 {@link %V} 的函数们。") {
                emitType(SimbotNames.listenerAno)
            }

            addAnnotation(SpringNames.componentAno.annotationRef())

            // 添加各种监听方法
            var showcaseNumber = 1

            // 1. 监听所有事件
            addMethod(createAllEventListenerMethod(showcaseNumber++))

            // 2. 过滤消息事件
            addMethod(createMessageEventListenerMethod(showcaseNumber++, javaStyle))

            // 3. 回复消息
            addMethod(createReplyMessageListenerMethod(showcaseNumber++, javaStyle))

            // 4. 组件特定的示例（如果有组件）
            val firstComponent = components.firstOrNull()
            if (firstComponent != null) {
                addMethod(createComponentEventListenerMethod(showcaseNumber, firstComponent, javaStyle))
            }
        }

        return JavaFile(handlerPackage, handlerClass)
    }

    /**
     * 生成完整的 Core 事件处理器类文件。
     *
     * @param packageName 基础包名
     * @param components 组件列表
     * @param javaStyle Java 编程风格
     * @return JavaFile 对象
     */
    fun createCoreEventHandlerFile(packageName: String, components: List<Component>, javaStyle: JavaStyle): JavaFile {
        val handlerPackage = "$packageName.$HANDLER_PACKAGE_SUFFIX".parseToPackageName()

        val handlerClass = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, DEFAULT_EVENT_HANDLER_CLASS_NAME) {
            addModifiers(JavaModifier.PUBLIC)

            addDoc("核心库事件处理器类。")

            // 添加各种监听方法
            var showcaseNumber = 1

            // 1. 监听所有事件
            addMethod(createAllEventListenerMethod(showcaseNumber++))

            // 2. 过滤消息事件
            addMethod(createMessageEventListenerMethod(showcaseNumber++, javaStyle))

            // 3. 回复消息
            addMethod(createReplyMessageListenerMethod(showcaseNumber++, javaStyle))

            // 4. 组件特定的示例（如果有组件）
            val firstComponent = components.firstOrNull()
            if (firstComponent != null) {
                addMethod(createComponentEventListenerMethod(showcaseNumber, firstComponent, javaStyle))
            }

            // 添加核心库特定注释
            addDoc("\n\n")
            addDoc("注意：核心库需要手动注册事件监听器到应用程序中\n")
            addDoc("可以通过 ApplicationBuilder 的配置方法进行注册")
        }

        return JavaFile(handlerPackage, handlerClass)
    }

    // ==================== 事件处理器方法模板 (codegentle-java) ====================

    /**
     * 生成监听所有事件的方法。
     *
     * @param showcaseNumber 示例编号
     * @return JavaMethodSpec 对象
     */
    fun createAllEventListenerMethod(showcaseNumber: Int): JavaMethodSpec {
        return JavaMethodSpec("handleAllAndPrint") {
            addModifiers(JavaModifier.PUBLIC)
            returns(JavaPrimitiveTypeNames.VOID.ref())

            addDoc(
                "示例$showcaseNumber: 监听所有的事件，然后将它们输出到控制台。\n\n" +
                        "@param event 你要监听的事件的类型。必须是一个 {@link %V} 类型的子类"
            ) {
                emitType(SimbotNames.eventClassName)
            }

            addAnnotation(SimbotNames.listenerAno)

            addParameter(
                JavaParameterSpec(
                    SimbotNames.eventClassName.ref(),
                    "event"
                )
            )

            addStatement("System.out.println(\"收到事件: \" + event)")
        }
    }

    /**
     * 生成消息事件监听器方法。
     *
     * @param showcaseNumber 示例编号
     * @param javaStyle Java 编程风格
     * @return JavaMethodSpec 对象
     */
    fun createMessageEventListenerMethod(showcaseNumber: Int, javaStyle: JavaStyle): JavaMethodSpec {
        return JavaMethodSpec("handleMessageEvent") {
            addModifiers(JavaModifier.PUBLIC)

            addDoc(
                "示例$showcaseNumber: 监听所有 **文本内容** 开头为 \"$DEFAULT_FILTER_TEXT\" 的消息事件，\n" +
                        "然后在控制台输出它的消息链内容。"
            )

            addAnnotation(SimbotNames.listenerAno)
            addAnnotation(SimbotNames.filterAno) {
                addMember("value", "%V", CodePart.string(DEFAULT_FILTER_TEXT))
            }

            addParameter(JavaParameterSpec(SimbotNames.msgEventClassName.ref(), "event"))

            if (javaStyle == JavaStyle.ASYNC) {
                returns(
                    ClassName("java.util.concurrent", "CompletableFuture")
                        .parameterized(WildcardTypeName().ref())
                        .ref()
                )
                // returns(ClassName("java.util.concurrent", "CompletableFuture").ref())
                addStatement(
                    """
                    return event.getMessageContent().getMessages().collectAsync()
                            .thenAccept(messages -> {
                                System.out.println("收到消息事件: " + event)
                                for (int i = 0; i < messages.size(); i++) {
                                    System.out.println("\t消息元素[" + i + "]: " + messages.get(i))
                                }
                            })
                """.trimIndent()
                )
            } else {
                returns(JavaPrimitiveTypeNames.VOID.ref())

                addStatement("System.out.println(\"收到消息事件: \" + event)")
                addStatement("")
                addStatement("var messages = event.getMessageContent().getMessages().toList()")
                addCode {
                    inControlFlow("for (int i = 0; i < messages.size(); i++)") {
                        addStatement("System.out.println(\"\\t消息元素[\" + i + \"]: \" + messages.get(i))")
                    }
                }
            }
        }
    }

    /**
     * 生成回复消息监听器方法。
     *
     * @param showcaseNumber 示例编号
     * @param javaStyle Java 编程风格
     * @return JavaMethodSpec 对象
     */
    fun createReplyMessageListenerMethod(showcaseNumber: Int, javaStyle: JavaStyle): JavaMethodSpec {
        return JavaMethodSpec("handleAndReply") {
            addModifiers(JavaModifier.PUBLIC)

            addDoc("示例$showcaseNumber: 监听消息事件并进行回复。")

            addAnnotation(SimbotNames.listenerAno.annotationRef())

            addParameter(
                JavaParameterSpec(
                    SimbotNames.contactMsgEventClassName.ref(),
                    "event"
                )
            )

            if (javaStyle == JavaStyle.ASYNC) {
                returns(
                    ClassName("java.util.concurrent", "CompletableFuture")
                        .parameterized(WildcardTypeName().ref())
                        .ref()
                )

                addStatement(
                    """
                    return event.replyAsync(%V)
                        .thenCompose(result -> event.getContent().sendAsync(%V))
                """.trimIndent()
                ) {
                    emitString(DEFAULT_REPLY_MESSAGE_1)
                    emitString(DEFAULT_REPLY_MESSAGE_2)
                }
            } else {
                returns(JavaPrimitiveTypeNames.VOID.ref())

                addComment("基于事件回复一句\"$DEFAULT_REPLY_MESSAGE_1\"")
                addStatement("event.replyBlocking(\"$DEFAULT_REPLY_MESSAGE_1\")")
                addStatement("")
                addComment("或直接根据 content 发送一句\"$DEFAULT_REPLY_MESSAGE_2\"")
                addStatement("event.getContent().sendBlocking(\"$DEFAULT_REPLY_MESSAGE_2\")")
            }
        }
    }

    /**
     * 生成组件特定事件监听器方法。
     *
     * @param showcaseNumber 示例编号
     * @param component 组件信息
     * @param javaStyle Java 编程风格
     * @return JavaMethodSpec 对象
     */
    fun createComponentEventListenerMethod(
        showcaseNumber: Int,
        component: Component,
        javaStyle: JavaStyle
    ): JavaMethodSpec {
        return JavaMethodSpec("handle${component.name}Event") {
            addModifiers(JavaModifier.PUBLIC)

            addDoc("示例$showcaseNumber: 处理 ${component.name} 组件相关的事件。")

            addAnnotation(SimbotNames.listenerAno.annotationRef())

            addParameter(
                JavaParameterSpec(
                    SimbotNames.eventClassName.ref(),
                    "event"
                )
            )

            if (javaStyle == JavaStyle.ASYNC) {
                returnsCompletableFuture()
                addStatement(
                    """
                    return CompletableFuture.runAsync(() -> {
                        System.out.println("处理 ${'$'}{component.name} 事件: " + event);
                    })
                """.trimIndent()
                )
            } else {
                returns(JavaPrimitiveTypeNames.VOID.ref())

                addStatement("System.out.println(\"处理 ${component.name} 事件: \" + event)")
            }
        }
    }

    private fun JavaMethodSpec.Builder.returnsCompletableFuture() {
        returns(
            ClassName("java.util.concurrent", "CompletableFuture")
                .parameterized(WildcardTypeName().ref())
                .ref()
        )
    }

    // ==================== 工具方法 ====================

    /**
     * 获取处理器文件名。
     *
     * @return 处理器文件名
     */
    fun getHandlerFileName(): String = "$DEFAULT_EVENT_HANDLER_CLASS_NAME.java"

    /**
     * 获取主应用类文件名。
     *
     * @param className 类名，默认为 [DEFAULT_MAIN_CLASS_NAME]
     * @return 主应用类文件名
     */
    fun getMainClassFileName(className: String = DEFAULT_MAIN_CLASS_NAME): String = "$className.java"
}