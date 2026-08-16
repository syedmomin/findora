package com.findora.app.ui.screens.scanner

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.findora.app.FindoraApplication
import com.findora.app.data.repository.DocumentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ScanState {
    data object Idle : ScanState
    data object Processing : ScanState
    data class Success(val documentId: Long) : ScanState
    data class Error(val message: String) : ScanState
}

class ScannerViewModel(
    private val documents: DocumentRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<ScanState>(ScanState.Idle)
    val state: StateFlow<ScanState> = _state.asStateFlow()

    /** Runs OCR + save on the picked/captured [image], then emits Success(id). */
    fun process(image: Uri) {
        if (_state.value is ScanState.Processing) return
        _state.value = ScanState.Processing
        viewModelScope.launch {
            runCatching { documents.scanAndSave(image) }
                .onSuccess { id -> _state.value = ScanState.Success(id) }
                .onFailure { e ->
                    _state.value = ScanState.Error(e.message ?: "Couldn't read text from that image.")
                }
        }
    }

    /** Renders + OCRs every page of the picked [pdf], saves one document, emits Success(id). */
    fun processPdf(pdf: Uri) {
        if (_state.value is ScanState.Processing) return
        _state.value = ScanState.Processing
        viewModelScope.launch {
            runCatching { documents.importPdfAndSave(pdf) }
                .onSuccess { id -> _state.value = ScanState.Success(id) }
                .onFailure { e ->
                    _state.value = ScanState.Error(e.message ?: "Couldn't read text from that PDF.")
                }
        }
    }

    fun dismissError() {
        if (_state.value is ScanState.Error) _state.value = ScanState.Idle
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as FindoraApplication
                ScannerViewModel(app.container.documentRepository)
            }
        }
    }
}
