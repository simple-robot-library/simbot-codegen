package love.forte.simbot.codegen.gen.core.generators.java

import love.forte.simbot.codegen.gen.core.Component
import love.forte.simbot.codegen.gen.core.JavaStyle

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
     * Spring Boot 主应用类模板。
     * 
     * @param packageName 包名
     * @param className 类名
     * @return 生成的 Spring Boot 主应用类代码
     */
    fun springMainClassTemplate(packageName: String, className: String): String = """
        |package $packageName;
        |
        |import love.forte.simbot.spring.EnableSimbot;
        |import org.springframework.boot.SpringApplication;
        |import org.springframework.boot.autoconfigure.SpringBootApplication;
        |
        |/**
        | * Spring程序的入口注解类。
        | * 添加 {@link EnableSimbot} 注解来标记启用 simbot 相关的功能。
        | */
        |@EnableSimbot
        |@SpringBootApplication
        |public class $className {
        |
        |    public static void main(String[] args) {
        |        SpringApplication.run($className.class, args);
        |    }
        |}
    """.trimMargin()
    
    /**
     * Spring 事件处理器类头部模板。
     * 
     * @param packageName 包名
     * @param includeAsync 是否包含异步导入
     * @return 生成的类头部代码
     */
    fun springEventHandlerHeaderTemplate(packageName: String, includeAsync: Boolean): String = buildString {
        val handlePackage = "$packageName.$HANDLER_PACKAGE_SUFFIX"
        appendLine("package $handlePackage;")
        appendLine()
        appendLine("import love.forte.simbot.annotations.Filter;")
        appendLine("import love.forte.simbot.annotations.Listener;")
        appendLine("import love.forte.simbot.event.Event;")
        appendLine("import love.forte.simbot.event.ContactMessageEvent;")
        appendLine("import love.forte.simbot.event.MessageEvent;")
        appendLine("import org.springframework.stereotype.Component;")
        if (includeAsync) {
            appendLine("import java.util.concurrent.CompletableFuture;")
        }
        appendLine()
        appendLine("/**")
        appendLine(" * 一个用于承载监听函数的事件处理器类。")
        appendLine(" * ")
        appendLine(" * 将它标记为 {@link Component} 以交由Spring进行管理，")
        appendLine(" * simbot-spring 会解析其中标记了 {@link Listener} 的函数们。")
        appendLine(" */")
        appendLine("@Component")
        appendLine("public class $DEFAULT_EVENT_HANDLER_CLASS_NAME {")
    }
    
    // ==================== Core 框架模板 ====================
    
    /**
     * 核心库主应用类模板。
     * 
     * @param packageName 包名
     * @param className 类名
     * @param javaStyle Java 编程风格
     * @return 生成的核心库主应用类代码
     */
    fun coreMainClassTemplate(packageName: String, className: String, javaStyle: JavaStyle): String = buildString {
        appendLine("package $packageName;")
        appendLine()
        appendLine("import love.forte.simbot.application.Application;")
        appendLine("import love.forte.simbot.application.ApplicationBuilder;")
        if (javaStyle == JavaStyle.ASYNC) {
            appendLine("import java.util.concurrent.CompletableFuture;")
        }
        appendLine()
        appendLine("/**")
        appendLine(" * 核心库程序的入口类。")
        appendLine(" */")
        appendLine("public class $className {")
        appendLine()
        if (javaStyle == JavaStyle.ASYNC) {
            appendLine("    public static CompletableFuture<Void> main(String[] args) {")
            appendLine("        return ApplicationBuilder.create()")
            appendLine("                .buildAsync()")
            appendLine("                .thenCompose(Application::joinAsync);")
        } else {
            appendLine("    public static void main(String[] args) throws Exception {")
            appendLine("        Application application = ApplicationBuilder.create().build();")
            appendLine("        application.join();")
        }
        appendLine("    }")
        appendLine("}")
    }
    
    /**
     * 核心库事件处理器类头部模板。
     * 
     * @param packageName 包名
     * @param includeAsync 是否包含异步导入
     * @return 生成的类头部代码
     */
    fun coreEventHandlerHeaderTemplate(packageName: String, includeAsync: Boolean): String = buildString {
        val handlePackage = "$packageName.$HANDLER_PACKAGE_SUFFIX"
        appendLine("package $handlePackage;")
        appendLine()
        appendLine("import love.forte.simbot.annotations.Filter;")
        appendLine("import love.forte.simbot.annotations.Listener;")
        appendLine("import love.forte.simbot.event.Event;")
        appendLine("import love.forte.simbot.event.ContactMessageEvent;")
        appendLine("import love.forte.simbot.event.MessageEvent;")
        if (includeAsync) {
            appendLine("import java.util.concurrent.CompletableFuture;")
        }
        appendLine()
        appendLine("/**")
        appendLine(" * 核心库事件处理器类。")
        appendLine(" */")
        appendLine("public class $DEFAULT_EVENT_HANDLER_CLASS_NAME {")
    }
    
    // ==================== 事件处理器方法模板 ====================
    
    /**
     * 所有事件监听器方法模板。
     * 
     * @param showcaseNumber 示例编号
     * @return 生成的方法代码
     */
    fun allEventListenerTemplate(showcaseNumber: Int): String = """
        |    /**
        |     * 示例$showcaseNumber: 监听所有的事件，然后将它们输出到控制台。
        |     * 
        |     * @param event 你要监听的事件的类型。必须是一个 {@link Event} 类型的子类
        |     */
        |    @Listener
        |    public void handleAllAndPrint(Event event) {
        |        System.out.println("收到事件: " + event);
        |    }
    """.trimMargin()
    
    /**
     * 消息事件监听器方法模板。
     * 
     * @param showcaseNumber 示例编号
     * @param javaStyle Java 编程风格
     * @return 生成的方法代码
     */
    fun messageEventListenerTemplate(showcaseNumber: Int, javaStyle: JavaStyle): String = buildString {
        appendLine("    /**")
        appendLine("     * 示例$showcaseNumber: 监听所有 **文本内容** 开头为 \"$DEFAULT_FILTER_TEXT\" 的消息事件，")
        appendLine("     * 然后在控制台输出它的消息链内容。")
        appendLine("     */")
        appendLine("    @Listener")
        appendLine("    @Filter(\"$DEFAULT_FILTER_TEXT\")")
        if (javaStyle == JavaStyle.ASYNC) {
            appendLine("    public CompletableFuture<Void> handleMessageEvent(MessageEvent event) {")
            appendLine("        return event.getMessageContent().getMessages().collectAsync()")
            appendLine("                .thenAccept(messages -> {")
            appendLine("                    System.out.println(\"收到消息事件: \" + event);")
            appendLine("                    for (int i = 0; i < messages.size(); i++) {")
            appendLine("                        System.out.println(\"\\t消息元素[\" + i + \"]: \" + messages.get(i));")
            appendLine("                    }")
            appendLine("                });")
        } else {
            appendLine("    public void handleMessageEvent(MessageEvent event) {")
            appendLine("        System.out.println(\"收到消息事件: \" + event);")
            appendLine("        ")
            appendLine("        var messages = event.getMessageContent().getMessages().collect();")
            appendLine("        for (int i = 0; i < messages.size(); i++) {")
            appendLine("            System.out.println(\"\\t消息元素[\" + i + \"]: \" + messages.get(i));")
            appendLine("        }")
        }
        appendLine("    }")
    }
    
    /**
     * 回复消息监听器方法模板。
     * 
     * @param showcaseNumber 示例编号
     * @param javaStyle Java 编程风格
     * @return 生成的方法代码
     */
    fun replyMessageListenerTemplate(showcaseNumber: Int, javaStyle: JavaStyle): String = buildString {
        appendLine("    /**")
        appendLine("     * 示例$showcaseNumber: 监听消息事件并进行回复。")
        appendLine("     */")
        appendLine("    @Listener")
        if (javaStyle == JavaStyle.ASYNC) {
            appendLine("    public CompletableFuture<Void> handleAndReply(ContactMessageEvent event) {")
            appendLine("        return event.replyAsync(\"$DEFAULT_REPLY_MESSAGE_1\")")
            appendLine("                .thenCompose(result -> event.getContent().sendAsync(\"$DEFAULT_REPLY_MESSAGE_2\"));")
        } else {
            appendLine("    public void handleAndReply(ContactMessageEvent event) {")
            appendLine("        // 基于事件回复一句\"$DEFAULT_REPLY_MESSAGE_1\"")
            appendLine("        event.reply(\"$DEFAULT_REPLY_MESSAGE_1\");")
            appendLine("        ")
            appendLine("        // 或直接根据 content 发送一句\"$DEFAULT_REPLY_MESSAGE_2\"")
            appendLine("        event.getContent().send(\"$DEFAULT_REPLY_MESSAGE_2\");")
        }
        appendLine("    }")
    }
    
    /**
     * 组件特定事件监听器方法模板。
     * 
     * @param showcaseNumber 示例编号
     * @param component 组件信息
     * @param javaStyle Java 编程风格
     * @return 生成的方法代码
     */
    fun componentEventListenerTemplate(showcaseNumber: Int, component: Component, javaStyle: JavaStyle): String = buildString {
        appendLine("    /**")
        appendLine("     * 示例$showcaseNumber: 处理 ${component.name} 组件相关的事件。")
        appendLine("     */")
        appendLine("    @Listener")
        if (javaStyle == JavaStyle.ASYNC) {
            appendLine("    public CompletableFuture<Void> handle${component.name}Event(Event event) {")
            appendLine("        return CompletableFuture.runAsync(() -> {")
            appendLine("            System.out.println(\"处理 ${component.name} 事件: \" + event);")
            appendLine("        });")
        } else {
            appendLine("    public void handle${component.name}Event(Event event) {")
            appendLine("        System.out.println(\"处理 ${component.name} 事件: \" + event);")
        }
        appendLine("    }")
    }
    
    /**
     * 类结束模板。
     */
    const val CLASS_CLOSE_TEMPLATE = "}"
    
    // ==================== 工具方法 ====================
    
    /**
     * 获取处理器包名。
     * 
     * @param basePackageName 基础包名
     * @return 处理器包名
     */
    fun getHandlerPackageName(basePackageName: String): String = "$basePackageName.$HANDLER_PACKAGE_SUFFIX"
    
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