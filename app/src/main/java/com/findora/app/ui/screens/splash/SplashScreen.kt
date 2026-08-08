package com.findora.app.ui.screens.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.findora.app.R
import com.findora.app.ui.theme.RoyalBlue
import kotlinx.coroutines.delay

/** Brand-blue splash with the centered geometric F. Subtle scale + fade, no text. */
@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (visible) 1f else 0.8f, tween(300), label = "scale")
    val alpha by animateFloatAsState(if (visible) 1f else 0f, tween(300), label = "alpha")

    LaunchedEffect(Unit) {
        visible = true
        delay(1000)
        onFinished()
    }

    Box(
        Modifier.fillMaxSize().background(RoyalBlue),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier.size(160.dp).scale(scale).alpha(alpha),
        )
    }
}
