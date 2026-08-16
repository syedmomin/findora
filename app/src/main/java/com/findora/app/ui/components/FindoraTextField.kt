package com.findora.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import com.findora.app.ui.theme.FindoraRadius
import com.findora.app.ui.theme.glassBorderColor
import com.findora.app.ui.theme.glassFill

/**
 * The one text input used across Findora's forms and dialogs — a rounded,
 * frosted-glass field matching [FindoraSearchBar]. Keeps every input consistent.
 */
@Composable
fun FindoraTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    singleLine: Boolean = true,
    leadingIcon: ImageVector? = null,
    imeAction: ImeAction = ImeAction.Done,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        shape = FindoraRadius.Search,
        label = label?.let { { Text(it) } },
        placeholder = placeholder?.let { { Text(it) } },
        leadingIcon = leadingIcon?.let { { Icon(it, contentDescription = null) } },
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = imeAction),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = glassBorderColor(),
            focusedContainerColor = glassFill(),
            unfocusedContainerColor = glassFill(),
        ),
    )
}
