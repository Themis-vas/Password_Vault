package com.passguard.app.domain.model

data class UserSettings(
    val useDarkTheme: Boolean = true,
    val autoLockTimeoutMinutes: Int = 1,
    val clipboardClearSeconds: Int = 30,
    val secureScreenEnabled: Boolean = true,
    val preferredLanguage: String = "system"
)
