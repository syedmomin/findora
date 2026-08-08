package com.findora.app.ui.screens.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.findora.app.FindoraApplication
import com.findora.app.data.model.Category
import com.findora.app.data.model.Document
import com.findora.app.data.repository.DocumentRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class CategoryViewModel(
    documents: DocumentRepository,
    val category: Category,
) : ViewModel() {

    val documents: StateFlow<List<Document>?> =
        documents.documentsIn(category)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    companion object {
        fun provideFactory(category: Category) = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as FindoraApplication
                CategoryViewModel(app.container.documentRepository, category)
            }
        }
    }
}
