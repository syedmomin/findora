package com.findora.app.ui.screens.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.findora.app.data.model.Category
import com.findora.app.ui.components.DocumentCard
import com.findora.app.ui.components.DocumentSkeletonList
import com.findora.app.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    categoryName: String,
    onBack: () -> Unit,
    onOpenDocument: (Long) -> Unit,
) {
    val category = Category.fromName(categoryName)
    val viewModel: CategoryViewModel = viewModel(factory = CategoryViewModel.provideFactory(category))
    val docs by viewModel.documents.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category.label) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        val list = docs
        when {
            list == null -> DocumentSkeletonList(Modifier.padding(padding).padding(horizontal = 20.dp, vertical = 8.dp))
            list.isEmpty() -> Box(Modifier.fillMaxSize().padding(padding)) {
                EmptyState(
                    icon = category.icon,
                    title = "No ${category.label.lowercase()} yet",
                    body = "Documents you file under ${category.label} will appear here.",
                )
            }
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 20.dp, end = 20.dp,
                    top = padding.calculateTopPadding() + 8.dp,
                    bottom = padding.calculateBottomPadding() + 24.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(list, key = { it.id }) { doc ->
                    DocumentCard(document = doc, onClick = { onOpenDocument(doc.id) })
                }
            }
        }
    }
}
