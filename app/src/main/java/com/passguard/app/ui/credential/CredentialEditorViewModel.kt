package com.passguard.app.ui.credential

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.passguard.app.domain.model.Credential
import com.passguard.app.domain.model.PasswordOptions
import com.passguard.app.domain.usecase.EvaluatePasswordStrengthUseCase
import com.passguard.app.domain.usecase.GeneratePasswordUseCase
import com.passguard.app.domain.usecase.GetCredentialUseCase
import com.passguard.app.domain.usecase.ObserveCategoriesUseCase
import com.passguard.app.domain.usecase.SaveCredentialUseCase
import com.passguard.app.domain.usecase.DecryptTextUseCase
import com.passguard.app.domain.usecase.EncryptTextUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class CredentialEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeCategoriesUseCase: ObserveCategoriesUseCase,
    private val saveCredentialUseCase: SaveCredentialUseCase,
    private val getCredentialUseCase: GetCredentialUseCase,
    private val encryptTextUseCase: EncryptTextUseCase,
    private val decryptTextUseCase: DecryptTextUseCase,
    private val generatePasswordUseCase: GeneratePasswordUseCase,
    private val evaluatePasswordStrengthUseCase: EvaluatePasswordStrengthUseCase
) : ViewModel() {

    private val credentialId: Long? = savedStateHandle.get<Long>("credentialId")

    private val state = MutableStateFlow(CredentialEditorUiState())

    val uiState: StateFlow<CredentialEditorUiState> = combine(
        state,
        observeCategoriesUseCase()
    ) { uiState, categories ->
        uiState.copy(categories = categories)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CredentialEditorUiState())

    init {
        credentialId?.let { id ->
            viewModelScope.launch {
                val credential = getCredentialUseCase(id)
                credential?.let { populateExisting(it) }
            }
        }
    }

    private suspend fun populateExisting(credential: Credential) {
        val password = decryptTextUseCase(credential.password)
        val notes = credential.notes?.let { decryptTextUseCase(it) } ?: ""
        state.value = state.value.copy(
            title = credential.title,
            username = credential.username,
            password = password,
            url = credential.url.orEmpty(),
            notes = notes,
            categoryId = credential.categoryId,
            favorite = credential.favorite,
            isEditMode = true,
            passwordStrength = evaluatePasswordStrengthUseCase(password)
        )
    }

    fun onTitleChange(value: String) {
        state.value = state.value.copy(title = value)
    }

    fun onUsernameChange(value: String) {
        state.value = state.value.copy(username = value)
    }

    fun onPasswordChange(value: String) {
        state.value = state.value.copy(
            password = value,
            passwordStrength = evaluatePasswordStrengthUseCase(value)
        )
    }

    fun onUrlChange(value: String) {
        state.value = state.value.copy(url = value)
    }

    fun onNotesChange(value: String) {
        state.value = state.value.copy(notes = value)
    }

    fun onCategoryChange(categoryId: Long?) {
        state.value = state.value.copy(categoryId = categoryId)
    }

    fun onFavoriteChange(favorite: Boolean) {
        state.value = state.value.copy(favorite = favorite)
    }

    fun generatePassword(options: PasswordOptions = PasswordOptions()) {
        val password = generatePasswordUseCase(options)
        onPasswordChange(password)
    }

    fun save() {
        val current = state.value
        if (current.title.isBlank() || current.password.isBlank()) {
            state.value = current.copy(error = "missing_fields")
            return
        }
        viewModelScope.launch {
            state.value = current.copy(isSaving = true)
            val encryptedPassword = encryptTextUseCase(current.password)
            val encryptedNotes = current.notes.takeIf { it.isNotBlank() }?.let { encryptTextUseCase(it) }
            val credential = Credential(
                id = credentialId ?: 0L,
                title = current.title,
                username = current.username,
                password = encryptedPassword,
                url = current.url.ifBlank { null },
                notes = encryptedNotes,
                categoryId = current.categoryId,
                favorite = current.favorite,
                createdAt = if (credentialId == null) System.currentTimeMillis() else getCredentialUseCase(credentialId!!)?.createdAt
                    ?: System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            runCatching { saveCredentialUseCase(credential) }
                .onSuccess {
                    state.value = current.copy(isSaving = false, saved = true)
                }
                .onFailure {
                    state.value = current.copy(isSaving = false, error = it.message)
                }
        }
    }
}
