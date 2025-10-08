package com.passguard.app.ui.generator

import androidx.lifecycle.ViewModel
import com.passguard.app.core.clipboard.SecureClipboardManager
import com.passguard.app.domain.model.PasswordOptions
import com.passguard.app.domain.model.PasswordStrength
import com.passguard.app.domain.usecase.EvaluatePasswordStrengthUseCase
import com.passguard.app.domain.usecase.GeneratePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class PasswordGeneratorViewModel @Inject constructor(
    private val generatePasswordUseCase: GeneratePasswordUseCase,
    private val evaluatePasswordStrengthUseCase: EvaluatePasswordStrengthUseCase,
    private val secureClipboardManager: SecureClipboardManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PasswordGeneratorUiState())
    val uiState: StateFlow<PasswordGeneratorUiState> = _uiState

    init {
        generate()
    }

    fun updateOptions(options: PasswordOptions) {
        _uiState.value = _uiState.value.copy(options = options)
        generate()
    }

    fun generate() {
        val password = generatePasswordUseCase(_uiState.value.options)
        _uiState.value = _uiState.value.copy(
            password = password,
            strength = evaluatePasswordStrengthUseCase(password)
        )
    }

    fun copyToClipboard() {
        secureClipboardManager.copySecure(_uiState.value.password, "PassGuard")
    }
}

data class PasswordGeneratorUiState(
    val options: PasswordOptions = PasswordOptions(),
    val password: String = "",
    val strength: PasswordStrength = PasswordStrength.VERY_WEAK
)
