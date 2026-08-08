package com.findora.app.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Article
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.RequestQuote
import androidx.compose.material.icons.rounded.StickyNote2
import androidx.compose.ui.graphics.vector.ImageVector

/** Document categories from the Findora spec. Stored by [name] in the DB. */
enum class Category(val label: String, val icon: ImageVector) {
    RECEIPT("Receipts", Icons.Rounded.ReceiptLong),
    ID("IDs", Icons.Rounded.Badge),
    BILL("Bills", Icons.Rounded.RequestQuote),
    INVOICE("Invoices", Icons.Rounded.Description),
    PDF("PDFs", Icons.Rounded.PictureAsPdf),
    SCREENSHOT("Screenshots", Icons.Rounded.Image),
    NOTE("Notes", Icons.Rounded.StickyNote2),
    OTHER("Other", Icons.Rounded.Article);

    companion object {
        fun fromName(value: String?): Category =
            entries.firstOrNull { it.name == value } ?: OTHER
    }
}
