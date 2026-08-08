package com.findora.app.ui.screens.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.findora.app.FindoraApplication
import com.findora.app.data.model.Category
import com.findora.app.data.repository.DocumentRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CategoriesViewModel(documents: DocumentRepository) : ViewModel() {

    /** Every category with its count (0 when empty), in enum order. */
    val counts: StateFlow<List<Pair<Category, Int>>> = documents.categoryCounts()
        .map { counts -> Category.entries.map { it to (counts[it] ?: 0) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as FindoraApplication
                CategoriesViewModel(app.container.documentRepository)
            }
        }
    }
}
