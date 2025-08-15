package love.forte.simbot.codegen.gen.core.generators.spring

import jszip.JSZip
import love.forte.simbot.codegen.gen.core.Component
import love.forte.simbot.codegen.gen.core.GenerationContext
import love.forte.simbot.codegen.gen.core.generators.SpringConfigurationGenerator
import love.forte.simbot.codegen.gen.genSpringApplicationConfig

/**
 * Spring 框架配置文件生成器的实现类。
 *
 * 负责生成 Spring 框架的配置文件，包括应用程序配置文件和组件配置文件。
 *
 * @author ForteScarlet
 */
class SpringConfigurationGeneratorImpl : SpringConfigurationGenerator {
    /**
     * 生成 Spring 应用程序配置文件。
     *
     * @param resourceDir 资源目录的 JSZip 对象
     * @param context 代码生成的上下文信息
     */
    override suspend fun generateApplicationYml(resourceDir: JSZip, context: GenerationContext) {
        val applicationYml = genSpringApplicationConfig()
        resourceDir.file("application.yml", applicationYml)
    }

    /**
     * 生成特定组件的配置文件。
     *
     * @param botsDir simbot-bots 目录的 JSZip 对象
     * @param component 组件
     * @param context 代码生成的上下文信息
     */
    override suspend fun generateComponentConfiguration(
        botsDir: JSZip,
        component: Component,
        context: GenerationContext
    ) {
        val configJson = genComponentConfigForComponent(component)
        botsDir.file("${component.name.lowercase()}-example.bot.json", configJson)
    }

    /**
     * 为特定组件生成配置文件内容。
     *
     * @param component 组件
     * @return 生成的配置文件内容
     */
    private fun genComponentConfigForComponent(component: Component): String {
        // 根据组件名称生成不同的配置文件内容
        return when (component.name.lowercase()) {
            "qq", "qqguild" -> genQQComponentConfig(component)
            "kook" -> genKookComponentConfig(component)
            "onebot", "onebot11" -> genOneBotComponentConfig(component)
            else -> genDefaultComponentConfig(component)
        }
    }

    /**
     * 生成 QQ 组件的配置文件内容。
     *
     * @param component 组件
     * @return 生成的配置文件内容
     */
    private fun genQQComponentConfig(component: Component): String {
        return """
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
    }

    /**
     * 生成 KOOK 组件的配置文件内容。
     *
     * @param component 组件
     * @return 生成的配置文件内容
     */
    private fun genKookComponentConfig(component: Component): String {
        return """
            |{
            |    "component": "simbot.kook",
            |    "ticket": {
            |        "clientId": "你的bot的client id",
            |        "token": "你的bot的ws token"
            |    }
            |}
        """.trimMargin()
    }

    /**
     * 生成 OneBot 组件的配置文件内容。
     *
     * @param component 组件
     * @return 生成的配置文件内容
     */
    private fun genOneBotComponentConfig(component: Component): String {
        return """
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

    /**
     * 生成默认组件的配置文件内容。
     *
     * @param component 组件
     * @return 生成的配置文件内容
     */
    private fun genDefaultComponentConfig(component: Component): String {
        return """
            |{
            |    "component": "simbot.${component.name.lowercase()}",
            |    "config": {
            |        // 请根据组件文档配置相应的参数
            |    }
            |}
        """.trimMargin()
    }
}
