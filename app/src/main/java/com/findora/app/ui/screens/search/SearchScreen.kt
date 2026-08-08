package com.findora.app.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.findora.app.R
import com.findora.app.ui.components.DocumentCard
import com.findora.app.ui.components.DocumentSkeletonList
import com.findora.app.ui.components.EmptyState
import com.findora.app.ui.components.FindoraSearchBar

@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onOpenDocument: (Long) -> Unit,
    viewModel: SearchViewModel = viewModel(factory = SearchViewModel.Factory),
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val recents by viewModel.recentSearches.collectAsStateWithLifecycle()

    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    androidx.compose.runtime.LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
            }
            Spacer(Modifier.width(4.dp))
            FindoraSearchBar(
                value = query,
                onValueChange = viewModel::onQueryChange,
                hint = stringResource(R.string.search_hint),
                onClear = viewModel::clear,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
            )
        }

        when (val s = state) {
            SearchUiState.Idle -> RecentSearches(
                recents = recents,
                onSelect = { viewModel.onRecentSelected(it) },
            )

            SearchUiState.Searching -> DocumentSkeletonList(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            )

            is SearchUiState.Results -> {
                if (s.results.isEmpty()) {
                    EmptyState(
                        icon = Icons.Rounded.SearchOff,
                        title = stringResource(R.string.no_results),
                        body = stringResource(R.string.no_results_body),
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(s.results, key = { it.document.id }) { result ->
                            DocumentCard(
                                document = result.document,
                                snippet = result.snippet,
                                highlights = result.highlights,
                                onClick = {
                                    keyboard?.hide()
                                    viewModel.onSubmit()
                                    onOpenDocument(result.document.id)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentSearches(recents: List<String>, onSelect: (String) -> Unit) {
    if (recents.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "Search your documents by name, number, email, date…",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(40.dp),
            )
        }
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
    ) {
        item {
            Text(
                stringResource(R.string.recent_searches),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }
        items(recents) { q ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(q) }
                    .padding(vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Rounded.History,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.width(16.dp))
                Text(q, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
