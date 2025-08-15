package love.forte.simbot.codegen.components

import androidx.compose.runtime.*
import kotlinx.browser.window

/**
 * 记住当前窗口大小的 Composable 函数
 */
@Composable
fun rememberWindowSize(): WindowSize {
    var windowSize by remember { mutableStateOf(WindowSize.Desktop) }

    DisposableEffect(Unit) {
        val resizeListener: (Any?) -> Unit = {
            val width = window.innerWidth
            windowSize = when {
                width < 600 -> WindowSize.Mobile
                width < 840 -> WindowSize.Tablet
                else -> WindowSize.Desktop
            }
        }

        // 初始化窗口大小
        resizeListener(null)

        // 添加窗口调整大小的监听器
        window.addEventListener("resize", resizeListener)

        // 清理监听器
        onDispose {
            window.removeEventListener("resize", resizeListener)
        }
    }

    return windowSize
}

/**
 * 窗口大小枚举类
 */
enum class WindowSize {
    Mobile, Tablet, Desktop
}
