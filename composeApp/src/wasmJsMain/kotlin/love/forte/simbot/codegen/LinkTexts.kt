package love.forte.simbot.codegen

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextDecoration

val DefaultTextLinkStyles = TextLinkStyles(
    style = SpanStyle(color = Color.Blue),
    hoveredStyle = SpanStyle(
        color = Color.Blue,
        textDecoration = TextDecoration.Underline
    ),
)

fun AnnotatedString.Builder.withLink(text: String, url: String) {
    withLink(
        LinkAnnotation.Url(
            url = url,
            styles = DefaultTextLinkStyles
        )
    ) {
        append(text)
    }
}
