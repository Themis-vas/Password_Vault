package com.passguard.app.core.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.getSystemService
import com.passguard.app.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Singleton
class SecureClipboardManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository
) {

    private val clipboardManager: ClipboardManager =
        context.getSystemService() ?: throw IllegalStateException("Clipboard service unavailable")

    private val scope = CoroutineScope(Dispatchers.Main.immediate)
    private var clearJob: Job? = null

    fun copySecure(text: String, label: String) {
        clipboardManager.setPrimaryClip(ClipData.newPlainText(label, text))
        scheduleClear()
    }

    private fun scheduleClear() {
        clearJob?.cancel()
        clearJob = scope.launch {
            val settings = settingsRepository.settings.first()
            val delaySeconds = settings.clipboardClearSeconds.coerceAtLeast(5)
            delay(delaySeconds.seconds)
            clipboardManager.clearPrimaryClip()
        }
    }

    fun clearNow() {
        clearJob?.cancel()
        clipboardManager.clearPrimaryClip()
    }

    fun dispose() {
        scope.cancel()
    }
}
