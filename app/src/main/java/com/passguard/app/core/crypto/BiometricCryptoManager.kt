package com.passguard.app.core.crypto

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.passguard.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

@Singleton
class BiometricCryptoManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun canAuthenticate(): Boolean {
        val manager = BiometricManager.from(context)
        return manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
            BiometricManager.BIOMETRIC_SUCCESS
    }

    suspend fun authenticate(activity: FragmentActivity): Boolean {
        if (!canAuthenticate()) return false
        return suspendCancellableCoroutine { continuation ->
            val executor = ContextCompat.getMainExecutor(context)
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(context.getString(R.string.biometric_unlock_title))
                .setSubtitle(context.getString(R.string.biometric_unlock_subtitle))
                .setNegativeButtonText(context.getString(R.string.action_cancel))
                .build()

            val prompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    if (continuation.isActive) {
                        continuation.resumeWithException(IllegalStateException(errString.toString()))
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    if (continuation.isActive) {
                        continuation.resume(true)
                    }
                }

                override fun onAuthenticationFailed() {
                    // do nothing; user can retry
                }
            })

            continuation.invokeOnCancellation { prompt.cancelAuthentication() }
            prompt.authenticate(promptInfo)
        }
    }
}
