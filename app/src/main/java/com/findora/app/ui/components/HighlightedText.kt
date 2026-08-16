package com.findora.app.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.findora.app.ui.theme.HighlightDark
import com.findora.app.ui.theme.HighlightLight

/** Renders [text] with the given character [highlights] emphasized. */
@Composable
fun HighlightedText(
    text: String,
    highlights: List<IntRange>,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
) {
    val highlightColor = if (isSystemInDarkTheme()) HighlightDark else HighlightLight
    val annotated = buildAnnotatedString {
        append(text)
        for (range in highlights) {
            val start = range.first.coerceIn(0, text.length)
            val end = (range.last + 1).coerceIn(start, text.length)
            if (start < end) {
                addStyle(
                    SpanStyle(
                        background = highlightColor,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    start, end,
                )
            }
        }
    }
    Text(
        text = annotated,
        modifier = modifier,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        style = LocalTextStyle.current,
        onTextLayout = onTextLayout,
    )
}
