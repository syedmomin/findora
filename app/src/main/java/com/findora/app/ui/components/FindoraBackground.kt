package com.findora.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.findora.app.ui.theme.RoyalBlue
import com.findora.app.ui.theme.SkyBlue

/**
 * The shared app backdrop: the theme background color with three soft brand-color
 * "blobs" that give the glass panels something colorful to frost over. Placed once,
 * behind the whole app.
 */
@Composable
fun FindoraBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val dark = isSystemInDarkTheme()
    val base = MaterialTheme.colorScheme.background
    val blobA = RoyalBlue.copy(alpha = if (dark) 0.28f else 0.18f)
    val blobB = SkyBlue.copy(alpha = if (dark) 0.24f else 0.16f)
    val blobC = RoyalBlue.copy(alpha = if (dark) 0.16f else 0.09f)

    Box(modifier.fillMaxSize().background(base)) {
        Canvas(Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            val blobs = listOf(
                Triple(blobA, Offset(w * 0.10f, h * 0.05f), w * 0.75f),
                Triple(blobB, Offset(w * 0.98f, h * 0.28f), w * 0.65f),
                Triple(blobC, Offset(w * 0.50f, h * 0.98f), w * 0.85f),
            )
            for ((color, center, r) in blobs) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(color, Color.Transparent),
                        center = center,
                        radius = r,
                    ),
                    radius = r,
                    center = center,
                )
            }
        }
        content()
    }
}
