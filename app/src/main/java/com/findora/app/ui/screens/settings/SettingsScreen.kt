package com.findora.app.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.findora.app.R
import com.findora.app.data.repository.ThemeMode
import com.findora.app.ui.components.FindoraDialog
import com.findora.app.ui.components.GlassCard
import com.findora.app.ui.theme.Spacing

@Composable
fun SettingsScreen(
    contentPadding: PaddingValues,
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory),
) {
    val theme by viewModel.themeMode.collectAsStateWithLifecycle()
    val count by viewModel.documentCount.collectAsStateWithLifecycle()

    var showTheme by remember { mutableStateOf(false) }
    var showPrivacy by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }
    var showStorage by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(
                start = 20.dp, end = 20.dp,
                top = contentPadding.calculateTopPadding() + 12.dp,
                bottom = contentPadding.calculateBottomPadding() + 24.dp,
            ),
    ) {
        Text(
            stringResource(R.string.settings),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 12.dp),
        )

        SettingsRow(Icons.Rounded.Palette, stringResource(R.string.settings_theme), themeLabel(theme)) { showTheme = true }
        SettingsRow(Icons.Rounded.Language, stringResource(R.string.settings_language), "English") { }
        SettingsRow(Icons.Rounded.TextFields, stringResource(R.string.settings_ocr), "Latin script · On-device") { }
        SettingsRow(Icons.Rounded.Storage, stringResource(R.string.settings_storage), "$count documents") { showStorage = true }
        SettingsRow(Icons.Rounded.Lock, stringResource(R.string.settings_privacy), "On-device only") { showPrivacy = true }
        SettingsRow(Icons.Rounded.Info, stringResource(R.string.settings_about), "Version 1.0") { showAbout = true }
    }

    if (showTheme) {
        ThemeDialog(current = theme, onSelect = { viewModel.setTheme(it); showTheme = false }, onDismiss = { showTheme = false })
    }
    if (showPrivacy) {
        InfoDialog(
            title = stringResource(R.string.settings_privacy),
            body = "Findora runs all text recognition and search directly on your device. " +
                "Your documents and their contents never leave your phone and are never uploaded.",
            onDismiss = { showPrivacy = false },
        )
    }
    if (showAbout) {
        InfoDialog(
            title = "Findora",
            body = "Version 1.0\n\nInstantly find information hidden inside your screenshots, receipts, IDs, and documents — privately, on your device.",
            onDismiss = { showAbout = false },
        )
    }
    if (showStorage) {
        FindoraDialog(
            onDismiss = { showStorage = false },
            title = stringResource(R.string.settings_storage),
            confirmText = "Clear search history",
            onConfirm = { viewModel.clearRecentSearches(); showStorage = false },
            dismissText = stringResource(R.string.cancel),
        ) {
            Text(
                "$count documents stored on this device.\n\nYou can clear your recent search history below.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SettingsRow(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    GlassCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(bottom = Spacing.md),
        contentPadding = PaddingValues(Spacing.lg),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.size(Spacing.lg))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ThemeDialog(current: ThemeMode, onSelect: (ThemeMode) -> Unit, onDismiss: () -> Unit) {
    FindoraDialog(
        onDismiss = onDismiss,
        title = stringResource(R.string.settings_theme),
        confirmText = "Done",
        onConfirm = onDismiss,
    ) {
        ThemeMode.entries.forEach { mode ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(selected = current == mode, onClick = { onSelect(mode) })
                    .padding(vertical = Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(selected = current == mode, onClick = { onSelect(mode) })
                Spacer(Modifier.size(Spacing.sm))
                Text(themeLabel(mode), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
private fun InfoDialog(title: String, body: String, onDismiss: () -> Unit) {
    FindoraDialog(
        onDismiss = onDismiss,
        title = title,
        confirmText = "OK",
        onConfirm = onDismiss,
    ) {
        Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun themeLabel(mode: ThemeMode): String = when (mode) {
    ThemeMode.SYSTEM -> stringResource(R.string.theme_system)
    ThemeMode.LIGHT -> stringResource(R.string.theme_light)
    ThemeMode.DARK -> stringResource(R.string.theme_dark)
}
