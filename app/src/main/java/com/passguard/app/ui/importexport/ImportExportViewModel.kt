package com.passguard.app.ui.importexport

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.passguard.app.domain.usecase.ExportVaultUseCase
import com.passguard.app.domain.usecase.ImportVaultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ImportExportViewModel @Inject constructor(
    private val exportVaultUseCase: ExportVaultUseCase,
    private val importVaultUseCase: ImportVaultUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImportExportUiState())
    val uiState: StateFlow<ImportExportUiState> = _uiState

    fun export(uri: Uri, password: String) {
        viewModelScope.launch {
            _uiState.value = ImportExportUiState(isProcessing = true)
            runCatching { exportVaultUseCase(uri, password) }
                .onSuccess { _uiState.value = ImportExportUiState(message = "export_success") }
                .onFailure { _uiState.value = ImportExportUiState(message = it.message) }
        }
    }

    fun import(uri: Uri, password: String) {
        viewModelScope.launch {
            _uiState.value = ImportExportUiState(isProcessing = true)
            runCatching { importVaultUseCase(uri, password) }
                .onSuccess { _uiState.value = ImportExportUiState(message = "import_success") }
                .onFailure { _uiState.value = ImportExportUiState(message = it.message) }
        }
    }
}
