package com.findora.app.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.findora.app.FindoraApplication
import com.findora.app.data.model.Document
import com.findora.app.data.repository.DocumentRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetailViewModel(
    private val documents: DocumentRepository,
    private val documentId: Long,
) : ViewModel() {

    val document: StateFlow<Document?> = documents.document(documentId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun rename(newTitle: String) {
        val current = document.value ?: return
        if (newTitle.isBlank()) return
        viewModelScope.launch { documents.rename(current, newTitle) }
    }

    fun delete(onDeleted: () -> Unit) {
        val current = document.value ?: return
        viewModelScope.launch {
            documents.delete(current)
            onDeleted()
        }
    }

    companion object {
        fun provideFactory(documentId: Long) = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as FindoraApplication
                DetailViewModel(app.container.documentRepository, documentId)
            }
        }
    }
}
