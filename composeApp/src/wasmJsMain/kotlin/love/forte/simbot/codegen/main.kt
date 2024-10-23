package love.forte.simbot.codegen

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.scene.SingleLayerComposeScene
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.dom.clear

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val root = document.body!!
    root.clear()

    ComposeViewport(root) {
        App()
    }
}
