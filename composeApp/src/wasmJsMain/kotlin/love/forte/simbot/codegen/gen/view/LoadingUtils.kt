package love.forte.simbot.codegen.gen.view

import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel

/**
 * 用于管理加载状态的计数器类
 */
class LoadingCounter : ViewModel() {
    private val loading = mutableIntStateOf(0)

    val count: Int
        get() = loading.value

    val hasLoading: Boolean
        get() = loading.value > 0

    fun addLoading() {
        loading.value++
    }

    fun removeLoading() {
        loading.value--
    }
}

/**
 * LoadingCounter 的扩展操作符，用于增加加载计数
 */
operator fun LoadingCounter.inc(): LoadingCounter = apply { addLoading() }

/**
 * LoadingCounter 的扩展操作符，用于减少加载计数
 */
operator fun LoadingCounter.dec(): LoadingCounter = apply { removeLoading() }