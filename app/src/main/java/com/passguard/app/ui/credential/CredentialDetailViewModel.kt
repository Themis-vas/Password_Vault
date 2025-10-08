package com.passguard.app.ui.credential

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.passguard.app.core.clipboard.SecureClipboardManager
import com.passguard.app.domain.usecase.DecryptTextUseCase
import com.passguard.app.domain.usecase.GetCategoryUseCase
import com.passguard.app.domain.usecase.GetCredentialUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CredentialDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCredentialUseCase: GetCredentialUseCase,
    private val getCategoryUseCase: GetCategoryUseCase,
    private val decryptTextUseCase: DecryptTextUseCase,
    private val secureClipboardManager: SecureClipboardManager
) : ViewModel() {

    private val credentialId: Long = checkNotNull(savedStateHandle.get<Long>("credentialId"))

    private val _uiState = MutableStateFlow(CredentialDetailUiState())
    val uiState: StateFlow<CredentialDetailUiState> = _uiState

    init {
        viewModelScope.launch {
            runCatching { getCredentialUseCase(credentialId) }
                .onSuccess { credential ->
                    if (credential != null) {
                        val categoryName = credential.categoryId?.let { id ->
                            getCategoryUseCase(id)?.name
                        }
                        _uiState.value = CredentialDetailUiState(
                            title = credential.title,
                            username = credential.username,
                            password = decryptTextUseCase(credential.password),
                            url = credential.url,
                            notes = credential.notes?.let { decryptTextUseCase(it) } ?: "",
                            categoryName = categoryName,
                            favorite = credential.favorite,
                            isLoading = false
                        )
                    } else {
                        _uiState.value = CredentialDetailUiState(error = "not_found", isLoading = false)
                    }
                }
                .onFailure {
                    _uiState.value = CredentialDetailUiState(error = it.message, isLoading = false)
                }
        }
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun copyPassword() {
        val password = _uiState.value.password
        if (password.isNotEmpty()) {
            secureClipboardManager.copySecure(password, _uiState.value.title)
        }
    }
}
