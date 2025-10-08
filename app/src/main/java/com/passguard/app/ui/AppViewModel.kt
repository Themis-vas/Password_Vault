package com.passguard.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.passguard.app.domain.model.LockState
import com.passguard.app.domain.model.UserSettings
import com.passguard.app.domain.repository.CategoryRepository
import com.passguard.app.domain.usecase.ClearPinUseCase
import com.passguard.app.domain.usecase.LockVaultUseCase
import com.passguard.app.domain.usecase.ObserveLockStateUseCase
import com.passguard.app.domain.usecase.ObserveSettingsUseCase
import com.passguard.app.domain.usecase.ShouldAutoLockUseCase
import com.passguard.app.domain.usecase.SetPinUseCase
import com.passguard.app.domain.usecase.UnlockVaultUseCase
import com.passguard.app.domain.usecase.UpdateSettingsUseCase
import com.passguard.app.domain.usecase.ValidatePinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AppViewModel @Inject constructor(
    observeLockStateUseCase: ObserveLockStateUseCase,
    observeSettingsUseCase: ObserveSettingsUseCase,
    private val validatePinUseCase: ValidatePinUseCase,
    private val setPinUseCase: SetPinUseCase,
    private val clearPinUseCase: ClearPinUseCase,
    private val lockVaultUseCase: LockVaultUseCase,
    private val shouldAutoLockUseCase: ShouldAutoLockUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    private val unlockVaultUseCase: UnlockVaultUseCase,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val lockState: StateFlow<LockState> = observeLockStateUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, LockState.Locked)

    val settings: StateFlow<UserSettings> = observeSettingsUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserSettings())

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            categoryRepository.insertDefaults()
            _isLoading.value = false
        }
    }

    fun unlockWithPin(pin: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = validatePinUseCase(pin)
            onResult(success)
        }
    }

    fun createPin(pin: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            runCatching { setPinUseCase(pin) }
                .onSuccess {
                    unlockVaultUseCase()
                    onResult(true)
                }
                .onFailure { onResult(false) }
        }
    }

    fun clearPin() {
        viewModelScope.launch { clearPinUseCase() }
    }

    fun requestLock() {
        viewModelScope.launch { lockVaultUseCase() }
    }

    fun unlockBiometric() {
        viewModelScope.launch { unlockVaultUseCase() }
    }

    fun handleAppBackground() {
        viewModelScope.launch {
            if (shouldAutoLockUseCase()) {
                lockVaultUseCase()
            }
        }
    }

    fun updateSecureScreen(enabled: Boolean) {
        viewModelScope.launch {
            updateSettingsUseCase { it.copy(secureScreenEnabled = enabled) }
        }
    }

    fun updateDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            updateSettingsUseCase { it.copy(useDarkTheme = enabled) }
        }
    }

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            updateSettingsUseCase { it.copy(preferredLanguage = language) }
        }
    }

    fun updateClipboardTimeout(seconds: Int) {
        viewModelScope.launch {
            updateSettingsUseCase { it.copy(clipboardClearSeconds = seconds) }
        }
    }

    fun updateAutoLock(minutes: Int) {
        viewModelScope.launch {
            updateSettingsUseCase { it.copy(autoLockTimeoutMinutes = minutes) }
        }
    }
}
