package com.findora.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = RoyalBlue,
    onPrimary = OnPrimary,
    secondary = SkyBlue,
    onSecondary = DarkNavy,
    tertiary = SkyBlue,
    background = BackgroundLight,
    onBackground = OnSurfaceLight,
    surface = BackgroundLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = DividerLight,
    outlineVariant = DividerLight,
    error = ErrorRed,
    onError = OnPrimary,
)

private val DarkColors = darkColorScheme(
    primary = RoyalBlue,
    onPrimary = OnPrimary,
    secondary = SkyBlue,
    onSecondary = DarkNavy,
    tertiary = SkyBlue,
    background = BackgroundDark,
    onBackground = OnSurfaceDark,
    surface = BackgroundDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = DividerDark,
    outlineVariant = DividerDark,
    error = ErrorRed,
    onError = OnPrimary,
)

@Composable
fun FindoraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = FindoraTypography,
        shapes = FindoraShapes,
        content = content,
    )
}
