package love.forte.simbot.codegen.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


/**
 * 定制化的输入框组件，提供统一的样式和交互体验
 */
@Composable
fun EnhancedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    placeholder: String? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    singleLine: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    val windowSize = rememberWindowSize()
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

//    val borderColor by animateColorAsState(
//        targetValue = when {
//            isError -> MaterialTheme.colorScheme.error
//            isFocused -> MaterialTheme.colorScheme.primary
//            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
//        },
//        animationSpec = tween(durationMillis = 200),
//        label = "边框颜色动画"
//    )

    val borderColor = when {
        isError -> MaterialTheme.colorScheme.error
        isFocused -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.65f)
    }

//    val backgroundColor = when {
//        isFocused -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
//        else -> MaterialTheme.colorScheme.surface
//    }

//    val backgroundColor by animateColorAsState(
//        targetValue = when {
//            isFocused -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
//            else -> MaterialTheme.colorScheme.surface
//        },
//        label = "背景颜色动画"
//    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        label = {
            Text(
                text = label,
                fontWeight = if (isFocused) FontWeight.Medium else FontWeight.Normal,
                // 在移动设备上使用更小的字体
                fontSize = when (windowSize) {
                    WindowSize.Mobile -> 14.sp
                    else -> 16.sp
                }
            )
        },
        placeholder = placeholder?.let { { Text(it) } },
        supportingText = supportingText,
        isError = isError,
        singleLine = singleLine,
        enabled = enabled,
        interactionSource = interactionSource,
        trailingIcon = trailingIcon,
        leadingIcon = leadingIcon,
        shape = RoundedCornerShape(
            // 在移动设备上使用更小的圆角
            when (windowSize) {
                WindowSize.Mobile -> 8.dp
                else -> 12.dp
            }
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = borderColor,
            unfocusedBorderColor = borderColor.copy(alpha = 0.7f),
//            focusedContainerColor = backgroundColor,
//            unfocusedContainerColor = backgroundColor,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            errorBorderColor = MaterialTheme.colorScheme.error,
            errorContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f),
            errorLabelColor = MaterialTheme.colorScheme.error,
            errorCursorColor = MaterialTheme.colorScheme.error,
            errorSupportingTextColor = MaterialTheme.colorScheme.error,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        ),
    )
}
