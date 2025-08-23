package love.forte.simbot.codegen.components

import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
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

@Composable
fun rememberMousePositionState(initValue: Offset? = null): MutableState<Offset?> =
    remember { mutableStateOf<Offset?>(initValue) }

/**
 * 鼠标位置追踪修饰符
 * 封装鼠标位置追踪逻辑，返回当前鼠标位置状态
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Modifier.mousePosition(mousePositionState: MutableState<Offset?>): Modifier {
    return this
        .onPointerEvent(PointerEventType.Move) { event ->
            mousePositionState.value = event.changes.firstOrNull()?.position
        }
        .onPointerEvent(PointerEventType.Enter) { event ->
            mousePositionState.value = event.changes.firstOrNull()?.position
        }
        .onPointerEvent(PointerEventType.Exit) {
            mousePositionState.value = null
        }
}
