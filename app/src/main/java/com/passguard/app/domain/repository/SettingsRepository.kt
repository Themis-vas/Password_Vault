package com.passguard.app.domain.repository

import com.passguard.app.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<UserSettings>
    suspend fun update(transform: (UserSettings) -> UserSettings)
}
