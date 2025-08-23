package love.forte.simbot.codegen

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.dom.clear

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // 不立即移除加载脚本和清空加载动画
    // 让字体加载过程来控制这些元素的移除
    
    val root = document.getElementById("codegen-root")!!

    ComposeViewport(root) {
        App()
    }
}
