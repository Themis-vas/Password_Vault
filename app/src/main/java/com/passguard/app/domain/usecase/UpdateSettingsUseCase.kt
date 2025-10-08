package com.passguard.app.domain.usecase

import com.passguard.app.domain.model.UserSettings
import com.passguard.app.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(transform: (UserSettings) -> UserSettings) {
        settingsRepository.update(transform)
    }
}
