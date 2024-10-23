package love.forte.simbot.codegen.gen


/**
 *
 * @author ForteScarlet
 */
enum class SimbotComponent(
    val display: String,
    val owner: String,
    val repo: String,
    val simpleId: String,
    val ktorRequired: Boolean,
    val doc: String,
    val botConfigDoc: String,
) {

    QQ(
        "QQ机器人",
        "simple-robot",
        "simbot-component-qq-guild",
        "qq",
        true,
        "https://simbot.forte.love/component-qq-guild.html",
        "https://simbot.forte.love/component-qq-guild-bot-config.html",
    ),
    KOOK(
        "KOOK机器人",
        "simple-robot",
        "simbot-component-kook",
        "kook",
        true,
        "https://simbot.forte.love/component-kook.html",
        "https://simbot.forte.love/component-kook-bot-config.html",
    ),
    OB(
        "OneBot",
        "simple-robot",
        "simbot-component-onebot",
        "onebot",
        true,
        "https://simbot.forte.love/component-onebot.html",
        "https://simbot.forte.love/component-onebot-v11-bot-config.html",
    )


}
