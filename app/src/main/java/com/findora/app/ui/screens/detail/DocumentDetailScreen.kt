package com.findora.app.ui.screens.detail

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DriveFileRenameOutline
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.findora.app.R
import com.findora.app.data.model.Document
import com.findora.app.data.search.highlightRanges
import com.findora.app.ui.components.HighlightedText
import com.findora.app.ui.util.formatRelativeTime
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentDetailScreen(
    documentId: Long,
    onBack: () -> Unit,
    query: String = "",
    viewModel: DetailViewModel = viewModel(factory = DetailViewModel.provideFactory(documentId)),
) {
    val document by viewModel.document.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var showRename by remember { mutableStateOf(false) }
    var showDelete by remember { mutableStateOf(false) }

    val copiedMsg = stringResource(R.string.copied)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(document?.title ?: "", maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showRename = true }) {
                        Icon(Icons.Rounded.DriveFileRenameOutline, contentDescription = stringResource(R.string.rename))
                    }
                    IconButton(onClick = { showDelete = true }) {
                        Icon(Icons.Rounded.Delete, contentDescription = stringResource(R.string.delete))
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.ui.graphics.Color.Transparent,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
    ) { padding ->
        val doc = document
        if (doc == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        // Terms to highlight (present only when opened from search). Ranges are
        // computed over the full recognized text; the effect below scrolls to the
        // first hit once the text has been laid out.
        val terms = remember(query) {
            query.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        }
        val ranges = remember(doc.ocrText, terms) {
            if (terms.isEmpty()) emptyList() else highlightRanges(doc.ocrText, terms)
        }
        var textTopPx by remember { mutableStateOf(0f) }
        var containerTopPx by remember { mutableStateOf(0f) }
        var textLayout by remember { mutableStateOf<TextLayoutResult?>(null) }
        var didScrollToMatch by remember(documentId, query) { mutableStateOf(false) }

        LaunchedEffect(ranges, textLayout, textTopPx, containerTopPx) {
            val layout = textLayout
            if (!didScrollToMatch && ranges.isNotEmpty() && layout != null && textTopPx > 0f) {
                val len = layout.layoutInput.text.length
                if (len > 0) {
                    val box = layout.getBoundingBox(ranges.first().first.coerceIn(0, len - 1))
                    // Match offset in the scrollable content = (text top − viewport top
                    // in root space) + the match's y inside the text.
                    val contentOffset = (textTopPx - containerTopPx) + box.top
                    scrollState.animateScrollTo((contentOffset - 140f).toInt().coerceAtLeast(0))
                    didScrollToMatch = true
                }
            }
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .onGloballyPositioned { containerTopPx = it.positionInRoot().y }
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (doc.imagePath != null) {
                AsyncImage(
                    model = java.io.File(doc.imagePath),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(20.dp)),
                )
            }

            Column {
                Text(doc.title, style = MaterialTheme.typography.headlineSmall)
                Text(
                    "${doc.category.label}  ·  ${formatRelativeTime(doc.createdAt)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Action row: Share + Copy (Rename/Delete live in the top bar).
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                com.findora.app.ui.components.SecondaryButton(
                    text = stringResource(R.string.share),
                    icon = Icons.Rounded.Share,
                    onClick = { shareDocument(context, doc) },
                    modifier = Modifier.weight(1f),
                )
                com.findora.app.ui.components.SecondaryButton(
                    text = stringResource(R.string.copy),
                    icon = Icons.Rounded.ContentCopy,
                    onClick = {
                        clipboard.setText(AnnotatedString(doc.ocrText))
                        scope.launch { snackbar.showSnackbar(copiedMsg) }
                    },
                    modifier = Modifier.weight(1f),
                )
            }

            Text(
                "Recognized text",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            if (ranges.isEmpty()) {
                Text(
                    doc.ocrText.ifBlank { "No text was recognized in this document." },
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.onGloballyPositioned { textTopPx = it.positionInRoot().y },
                )
            } else {
                ProvideTextStyle(MaterialTheme.typography.bodyLarge) {
                    HighlightedText(
                        text = doc.ocrText,
                        highlights = ranges,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { textTopPx = it.positionInRoot().y },
                        onTextLayout = { textLayout = it },
                    )
                }
            }
        }
    }

    if (showRename) {
        RenameDialog(
            initial = document?.title.orEmpty(),
            onConfirm = { viewModel.rename(it); showRename = false },
            onDismiss = { showRename = false },
        )
    }
    if (showDelete) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            title = { Text(stringResource(R.string.delete_confirm_title)) },
            text = { Text(stringResource(R.string.delete_confirm_body)) },
            confirmButton = {
                TextButton(onClick = {
                    showDelete = false
                    viewModel.delete(onDeleted = onBack)
                }) { Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDelete = false }) { Text(stringResource(R.string.cancel)) }
            },
        )
    }
}

@Composable
private fun RenameDialog(initial: String, onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var text by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.rename_document)) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = { TextButton(onClick = { onConfirm(text) }) { Text(stringResource(R.string.save)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } },
    )
}

private fun shareDocument(context: android.content.Context, document: Document) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, document.title)
        putExtra(Intent.EXTRA_TEXT, document.ocrText)
    }
    context.startActivity(Intent.createChooser(intent, document.title))
}
