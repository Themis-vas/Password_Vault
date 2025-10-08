package com.passguard.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.passguard.app.R
import com.passguard.app.ui.AppViewModel

@Composable
fun SettingsRoute(
    appViewModel: AppViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settings by appViewModel.settings.collectAsStateWithLifecycle()
    SettingsScreen(
        darkTheme = settings.useDarkTheme,
        secureScreen = settings.secureScreenEnabled,
        autoLockMinutes = settings.autoLockTimeoutMinutes,
        clipboardSeconds = settings.clipboardClearSeconds,
        language = settings.preferredLanguage,
        onToggleDarkTheme = appViewModel::updateDarkTheme,
        onToggleSecureScreen = appViewModel::updateSecureScreen,
        onAutoLockChange = appViewModel::updateAutoLock,
        onClipboardChange = appViewModel::updateClipboardTimeout,
        onLanguageChange = appViewModel::updateLanguage,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun SettingsScreen(
    darkTheme: Boolean,
    secureScreen: Boolean,
    autoLockMinutes: Int,
    clipboardSeconds: Int,
    language: String,
    onToggleDarkTheme: (Boolean) -> Unit,
    onToggleSecureScreen: (Boolean) -> Unit,
    onAutoLockChange: (Int) -> Unit,
    onClipboardChange: (Int) -> Unit,
    onLanguageChange: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.title_settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingToggle(
                title = stringResource(R.string.setting_dark_theme),
                checked = darkTheme,
                onCheckedChange = onToggleDarkTheme
            )
            SettingToggle(
                title = stringResource(R.string.setting_secure_screen),
                checked = secureScreen,
                onCheckedChange = onToggleSecureScreen
            )
            Text(text = stringResource(R.string.setting_autolock_minutes, autoLockMinutes))
            Slider(
                value = autoLockMinutes.toFloat(),
                onValueChange = { onAutoLockChange(it.toInt().coerceIn(1, 15)) },
                valueRange = 1f..15f
            )
            Text(text = stringResource(R.string.setting_clipboard_seconds, clipboardSeconds))
            Slider(
                value = clipboardSeconds.toFloat(),
                onValueChange = { onClipboardChange(it.toInt().coerceIn(10, 120)) },
                valueRange = 10f..120f
            )
            Text(text = stringResource(R.string.setting_language_label, language))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                LanguageButton(code = "system", current = language, onLanguageChange = onLanguageChange)
                LanguageButton(code = "en", current = language, onLanguageChange = onLanguageChange)
                LanguageButton(code = "el", current = language, onLanguageChange = onLanguageChange)
                LanguageButton(code = "zh", current = language, onLanguageChange = onLanguageChange)
            }
        }
    }
}

@Composable
private fun SettingToggle(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
        }
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun LanguageButton(code: String, current: String, onLanguageChange: (String) -> Unit) {
    val label = when (code) {
        "system" -> stringResource(R.string.language_system)
        "en" -> stringResource(R.string.language_en)
        "el" -> stringResource(R.string.language_el)
        "zh" -> stringResource(R.string.language_zh)
        else -> code
    }
    Button(onClick = { onLanguageChange(code) }, enabled = current != code) {
        Text(text = label)
    }
}
