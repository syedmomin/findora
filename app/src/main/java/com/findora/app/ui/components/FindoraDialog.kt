package com.findora.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import com.findora.app.ui.theme.FindoraRadius
import com.findora.app.ui.theme.Spacing

/**
 * The one dialog used across Findora: a rounded, elevated sheet with a title,
 * free-form [content], a full-width primary action, and an optional text dismiss
 * below it. Pass [destructive] = true to render the action as a [DangerButton].
 */
@Composable
fun FindoraDialog(
    onDismiss: () -> Unit,
    title: String,
    confirmText: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    dismissText: String? = null,
    destructive: Boolean = false,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = FindoraRadius.Sheet,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = Spacing.sm,
        ) {
            Column(Modifier.padding(Spacing.xxl)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Column(Modifier.padding(top = Spacing.md), content = content)
                if (destructive) {
                    DangerButton(
                        text = confirmText,
                        onClick = onConfirm,
                        modifier = Modifier.fillMaxWidth().padding(top = Spacing.xl),
                    )
                } else {
                    PrimaryButton(
                        text = confirmText,
                        onClick = onConfirm,
                        modifier = Modifier.fillMaxWidth().padding(top = Spacing.xl),
                    )
                }
                if (dismissText != null) {
                    Column(
                        Modifier.fillMaxWidth().padding(top = Spacing.xs),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        TextButton(onClick = onDismiss) { Text(dismissText) }
                    }
                }
            }
        }
    }
}
