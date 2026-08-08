package com.findora.app.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.findora.app.R
import com.findora.app.data.model.Category
import com.findora.app.ui.components.CategoryPill
import com.findora.app.ui.components.DocumentCard
import com.findora.app.ui.components.DocumentSkeletonList
import com.findora.app.ui.components.EmptyState
import com.findora.app.ui.components.FindoraSearchBar
import com.findora.app.ui.components.SectionHeader
import java.util.Calendar

@Composable
fun HomeScreen(
    contentPadding: PaddingValues,
    onOpenSearch: () -> Unit,
    onOpenDocument: (Long) -> Unit,
    onOpenCategory: (Category) -> Unit,
    onSeeAllCategories: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = contentPadding.calculateTopPadding() + 16.dp,
            bottom = contentPadding.calculateBottomPadding() + 96.dp, // room for the FAB
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(greeting(), style = MaterialTheme.typography.displaySmall)
            Text(
                stringResource(R.string.home_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        item {
            FindoraSearchBar(
                value = "",
                onValueChange = {},
                hint = stringResource(R.string.search_hint),
                readOnly = true,
                onClick = onOpenSearch,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        if (state.recentSearches.isNotEmpty()) {
            item {
                SectionHeader(stringResource(R.string.recent_searches))
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.recentSearches) { q ->
                        AssistChip(
                            onClick = onOpenSearch,
                            label = { Text(q) },
                            leadingIcon = { Icon(Icons.Rounded.History, contentDescription = null) },
                        )
                    }
                }
            }
        }

        item {
            SectionHeader(
                stringResource(R.string.quick_categories),
                actionLabel = stringResource(R.string.see_all),
                onAction = onSeeAllCategories,
            )
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(Category.entries.toList()) { category ->
                    CategoryPill(category = category, onClick = { onOpenCategory(category) })
                }
            }
        }

        item {
            SectionHeader(stringResource(R.string.recent_documents))
        }

        val docs = state.recentDocuments
        when {
            docs == null -> item { DocumentSkeletonList() }
            docs.isEmpty() -> item {
                Column(Modifier.fillMaxWidth().height(280.dp)) {
                    EmptyState(
                        icon = Icons.Rounded.Search,
                        title = stringResource(R.string.empty_documents_title),
                        body = stringResource(R.string.empty_documents_body),
                    )
                }
            }
            else -> items(docs, key = { it.id }) { doc ->
                DocumentCard(document = doc, onClick = { onOpenDocument(doc.id) })
            }
        }
    }
}

private fun greeting(): String {
    // Not localized via resources because it varies by hour; kept simple.
    return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 5..11 -> "Good morning"
        in 12..17 -> "Good afternoon"
        else -> "Good evening"
    }
}
