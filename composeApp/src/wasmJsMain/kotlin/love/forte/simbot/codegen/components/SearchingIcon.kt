package love.forte.simbot.codegen.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


/**
 * 带动画效果的搜索图标组件
 */
@Composable
fun SearchingIcon(
    initialColor: Color = LocalContentColor.current,
    targetColor: Color = LocalContentColor.current.copy(alpha = .2f),
    animationSpec: InfiniteRepeatableSpec<Color> = infiniteRepeatable(
        tween(durationMillis = 600, delayMillis = 200),
        repeatMode = RepeatMode.Reverse
    ),
    label: String = "SearchIconColorAnimation"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "SearchIconTransition")
    val color by infiniteTransition.animateColor(
        initialValue = initialColor,
        targetValue = targetColor,
        animationSpec = animationSpec,
        label = label
    )
    val size by infiniteTransition.animateFloat(
        initialValue = 24f,
        targetValue = 28f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 800, delayMillis = 100),
            repeatMode = RepeatMode.Reverse
        ),
        label = "IconSizeAnimation"
    )

    Icon(
        Icons.Outlined.Search,
        "Searching",
        tint = color,
        modifier = Modifier.size(size.dp)
    )
}
