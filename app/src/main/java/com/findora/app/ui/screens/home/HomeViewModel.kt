package com.findora.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.findora.app.FindoraApplication
import com.findora.app.data.model.Document
import com.findora.app.data.repository.DocumentRepository
import com.findora.app.data.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val recentDocuments: List<Document>? = null, // null = loading
    val recentSearches: List<String> = emptyList(),
)

class HomeViewModel(
    documents: DocumentRepository,
    settings: SettingsRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> =
        kotlinx.coroutines.flow.combine(
            documents.recentDocuments(limit = 10),
            settings.recentSearches,
        ) { docs, searches ->
            HomeUiState(recentDocuments = docs, recentSearches = searches)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState(),
        )

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as FindoraApplication
                HomeViewModel(app.container.documentRepository, app.container.settingsRepository)
            }
        }
    }
}
