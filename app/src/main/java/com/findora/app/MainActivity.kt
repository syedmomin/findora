package com.findora.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.findora.app.data.repository.ThemeMode
import com.findora.app.ui.components.FindoraBackground
import com.findora.app.ui.navigation.FindoraApp
import com.findora.app.ui.theme.FindoraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Replace the brand-blue splash window background with the real app theme.
        setTheme(R.style.Theme_Findora)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val settings = (application as FindoraApplication).container.settingsRepository

        setContent {
            val themeMode by settings.themeMode.collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM)
            val dark = when (themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            FindoraTheme(darkTheme = dark) {
                FindoraBackground {
                    FindoraApp(themeMode = themeMode)
                }
            }
        }
    }
}
