package com.passguard.app.data.lock

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.passguard.app.domain.model.LockState
import com.passguard.app.domain.repository.LockRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

@Singleton
class LockRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : LockRepository {

    private val prefs = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val _lockState = MutableStateFlow(loadInitialState())
    override val lockState: Flow<LockState> = _lockState.asStateFlow()

    override suspend fun setPin(pin: String) = withContext(ioDispatcher) {
        require(pin.length in 4..8) { "Invalid PIN length" }
        val salt = ByteArray(SALT_SIZE)
        SecureRandom().nextBytes(salt)
        val hash = hashPin(pin, salt)
        prefs.edit()
            .putString(KEY_PIN_HASH, hash)
            .putString(KEY_PIN_SALT, salt.joinToString(separator = ",") { it.toString() })
            .apply()
        _lockState.value = LockState.Locked
    }

    override suspend fun clearPin() = withContext(ioDispatcher) {
        prefs.edit().remove(KEY_PIN_HASH).remove(KEY_PIN_SALT).apply()
        _lockState.value = LockState.NoPinSet
    }

    override suspend fun validatePin(pin: String): Boolean = withContext(ioDispatcher) {
        val storedHash = prefs.getString(KEY_PIN_HASH, null) ?: return@withContext false
        val storedSalt = prefs.getString(KEY_PIN_SALT, null)?.split(",")?.mapNotNull { it.toByteOrNull() }
            ?.toByteArray() ?: return@withContext false
        val computed = hashPin(pin, storedSalt)
        val success = computed == storedHash
        if (success) {
            markUnlocked()
            _lockState.value = LockState.Unlocked
        }
        success
    }

    override suspend fun unlock() = withContext(ioDispatcher) {
        if (prefs.contains(KEY_PIN_HASH)) {
            prefs.edit().putLong(KEY_LAST_UNLOCK, System.currentTimeMillis()).apply()
            _lockState.value = LockState.Unlocked
        } else {
            _lockState.value = LockState.NoPinSet
        }
    }

    override suspend fun markUnlocked() = withContext(ioDispatcher) {
        prefs.edit().putLong(KEY_LAST_UNLOCK, System.currentTimeMillis()).apply()
    }

    override suspend fun lock() = withContext(ioDispatcher) {
        if (prefs.contains(KEY_PIN_HASH)) {
            _lockState.value = LockState.Locked
        } else {
            _lockState.value = LockState.NoPinSet
        }
    }

    override suspend fun shouldAutoLock(): Boolean = withContext(ioDispatcher) {
        val timeoutMinutes = prefs.getInt(KEY_TIMEOUT_MINUTES, DEFAULT_TIMEOUT_MINUTES)
        val lastUnlock = prefs.getLong(KEY_LAST_UNLOCK, 0L)
        if (lastUnlock == 0L) {
            true
        } else {
            val elapsed = System.currentTimeMillis() - lastUnlock
            elapsed > timeoutMinutes * 60_000L
        }
    }

    override suspend fun updateTimeout(minutes: Int) = withContext(ioDispatcher) {
        prefs.edit().putInt(KEY_TIMEOUT_MINUTES, minutes).apply()
    }

    private fun loadInitialState(): LockState {
        return if (prefs.getString(KEY_PIN_HASH, null).isNullOrEmpty()) {
            LockState.NoPinSet
        } else {
            LockState.Locked
        }
    }

    private fun hashPin(pin: String, salt: ByteArray): String {
        val spec = PBEKeySpec(pin.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val hash = factory.generateSecret(spec).encoded
        return hash.joinToString(separator = ",") { it.toString() }
    }

    private fun String.toByteOrNull(): Byte? = toIntOrNull()?.toByte()

    private companion object {
        const val PREFS_NAME = "passguard_lock"
        const val KEY_PIN_HASH = "pin_hash"
        const val KEY_PIN_SALT = "pin_salt"
        const val KEY_LAST_UNLOCK = "last_unlock"
        const val KEY_TIMEOUT_MINUTES = "timeout_minutes"
        const val SALT_SIZE = 16
        const val PBKDF2_ITERATIONS = 48000
        const val KEY_LENGTH = 256
        const val DEFAULT_TIMEOUT_MINUTES = 1
    }
}
