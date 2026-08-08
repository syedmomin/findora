package com.findora.app.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** A single shimmering placeholder block. */
@Composable
fun SkeletonBox(modifier: Modifier = Modifier, shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(12.dp)) {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val alpha by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "alpha",
    )
    val base = MaterialTheme.colorScheme.onSurfaceVariant
    androidx.compose.foundation.layout.Box(
        modifier
            .clip(shape)
            .background(base.copy(alpha = alpha * 0.25f)),
    )
}

/** Placeholder list used while documents/search results load (spec: skeletons, not spinners). */
@Composable
fun DocumentSkeletonList(modifier: Modifier = Modifier, rows: Int = 5) {
    Column(modifier.fillMaxWidth()) {
        repeat(rows) {
            Row(Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
                SkeletonBox(Modifier.size(52.dp), RoundedCornerShape(14.dp))
                Spacer(Modifier.width(14.dp))
                Column(Modifier.padding(top = 4.dp)) {
                    SkeletonBox(Modifier.height(14.dp).width(180.dp))
                    Spacer(Modifier.height(10.dp))
                    SkeletonBox(Modifier.height(12.dp).width(240.dp))
                }
            }
        }
    }
}
