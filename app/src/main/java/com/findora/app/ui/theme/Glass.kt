package com.findora.app.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

// Frosted-glass palette. Translucent fills sit over the gradient backdrop
// (FindoraBackground) to read as glass; the light top border is the "edge".
val GlassFillLight = Color.White.copy(alpha = 0.58f)
val GlassFillDark = Color.White.copy(alpha = 0.07f)
val GlassBorderLight = Color.White.copy(alpha = 0.70f)
val GlassBorderDark = Color.White.copy(alpha = 0.16f)

@Composable
fun glassFill(): Color = if (isSystemInDarkTheme()) GlassFillDark else GlassFillLight

@Composable
fun glassBorderColor(): Color = if (isSystemInDarkTheme()) GlassBorderDark else GlassBorderLight

/**
 * Applies the shared frosted-glass treatment: a translucent fill plus a subtle
 * top-lit hairline border. Use on cards, bars, chips — anything that should feel
 * like a pane of glass floating over the gradient background.
 */
@Composable
fun Modifier.glassSurface(
    shape: Shape = RoundedCornerShape(20.dp),
    fill: Color = glassFill(),
): Modifier {
    val edge = glassBorderColor()
    val borderBrush = Brush.verticalGradient(
        listOf(edge, edge.copy(alpha = edge.alpha * 0.35f)),
    )
    return this
        .background(fill, shape)
        .border(BorderStroke(1.dp, borderBrush), shape)
}
