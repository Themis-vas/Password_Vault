package com.passguard.app.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.passguard.app.domain.model.UserSettings
import com.passguard.app.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    override val settings: Flow<UserSettings> = dataStore.data.map { preferences ->
        preferences.toUserSettings()
    }

    override suspend fun update(transform: (UserSettings) -> UserSettings) {
        dataStore.edit { preferences ->
            val current = preferences.toUserSettings()
            val updated = transform(current)
            preferences[USE_DARK_THEME] = updated.useDarkTheme
            preferences[AUTO_LOCK_MINUTES] = updated.autoLockTimeoutMinutes
            preferences[CLIPBOARD_SECONDS] = updated.clipboardClearSeconds
            preferences[SECURE_SCREEN] = updated.secureScreenEnabled
            preferences[PREFERRED_LANGUAGE] = updated.preferredLanguage
        }
    }

    private fun Preferences.toUserSettings(): UserSettings = UserSettings(
        useDarkTheme = this[USE_DARK_THEME] ?: true,
        autoLockTimeoutMinutes = this[AUTO_LOCK_MINUTES] ?: 1,
        clipboardClearSeconds = this[CLIPBOARD_SECONDS] ?: 30,
        secureScreenEnabled = this[SECURE_SCREEN] ?: true,
        preferredLanguage = this[PREFERRED_LANGUAGE] ?: "system"
    )

    private companion object {
        val USE_DARK_THEME = booleanPreferencesKey("use_dark_theme")
        val AUTO_LOCK_MINUTES = intPreferencesKey("auto_lock_minutes")
        val CLIPBOARD_SECONDS = intPreferencesKey("clipboard_seconds")
        val SECURE_SCREEN = booleanPreferencesKey("secure_screen")
        val PREFERRED_LANGUAGE = stringPreferencesKey("preferred_language")
    }
}
