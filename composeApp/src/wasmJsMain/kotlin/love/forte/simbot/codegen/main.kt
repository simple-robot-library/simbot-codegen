package love.forte.simbot.codegen

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.dom.clear

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    document.getElementById("codegen-root-load-script")?.remove()
    document.getElementById("codegen-root")?.clear()

    val root = document.body!!

    ComposeViewport(root) {
        App()
    }
}
