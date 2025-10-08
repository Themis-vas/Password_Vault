package com.passguard.app.data.transfer

import android.content.Context
import android.net.Uri
import android.util.Base64
import com.lambdaworks.crypto.SCrypt
import com.passguard.app.data.local.PassGuardDatabase
import com.passguard.app.data.local.dao.CategoryDao
import com.passguard.app.data.local.dao.CredentialDao
import com.passguard.app.data.local.entity.CategoryEntity
import com.passguard.app.data.local.entity.CredentialEntity
import com.passguard.app.domain.repository.VaultTransferRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.room.withTransaction
import org.json.JSONArray
import org.json.JSONObject

@Singleton
class VaultTransferRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val credentialDao: CredentialDao,
    private val categoryDao: CategoryDao,
    private val database: PassGuardDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : VaultTransferRepository {

    override suspend fun exportVault(uri: Uri, password: String) = withContext(ioDispatcher) {
        val credentials = credentialDao.getAllOnce()
        val categories = categoryDao.getAllOnce()
        val payload = JSONObject().apply {
            put("version", EXPORT_VERSION)
            put("createdAt", System.currentTimeMillis())
            put("credentials", JSONArray().apply {
                credentials.forEach { entity ->
                    put(entity.toJson())
                }
            })
            put("categories", JSONArray().apply {
                categories.forEach { entity ->
                    put(entity.toJson())
                }
            })
        }

        val salt = ByteArray(SALT_SIZE).also { SecureRandom().nextBytes(it) }
        val iv = ByteArray(IV_SIZE).also { SecureRandom().nextBytes(it) }
        val key = deriveKey(password, salt)
        val cipherText = encrypt(payload.toString().toByteArray(Charsets.UTF_8), key, iv)

        val packageJson = JSONObject().apply {
            put("version", EXPORT_VERSION)
            put("salt", Base64.encodeToString(salt, Base64.NO_WRAP))
            put("iv", Base64.encodeToString(iv, Base64.NO_WRAP))
            put("cipher", Base64.encodeToString(cipherText, Base64.NO_WRAP))
        }

        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(packageJson.toString().toByteArray(Charsets.UTF_8))
        } ?: throw IllegalStateException("Unable to open destination for export")
    }

    override suspend fun importVault(uri: Uri, password: String) = withContext(ioDispatcher) {
        val raw = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: throw IllegalStateException("Unable to open import file")
        val json = JSONObject(String(raw, Charsets.UTF_8))
        val version = json.getInt("version")
        require(version == EXPORT_VERSION) { "Unsupported backup version" }
        val salt = Base64.decode(json.getString("salt"), Base64.NO_WRAP)
        val iv = Base64.decode(json.getString("iv"), Base64.NO_WRAP)
        val cipher = Base64.decode(json.getString("cipher"), Base64.NO_WRAP)
        val key = deriveKey(password, salt)
        val decrypted = decrypt(cipher, key, iv)
        val payload = JSONObject(String(decrypted, Charsets.UTF_8))
        val credentialArray = payload.getJSONArray("credentials")
        val categoryArray = payload.getJSONArray("categories")

        database.withTransaction {
            credentialDao.deleteAll()
            categoryDao.deleteAll()

            for (i in 0 until categoryArray.length()) {
                val item = categoryArray.getJSONObject(i)
                val entity = CategoryEntity(
                    id = item.optLong("id", 0L),
                    name = item.getString("name"),
                    iconRes = item.getString("iconRes")
                )
                categoryDao.upsert(entity)
            }

            for (i in 0 until credentialArray.length()) {
                val item = credentialArray.getJSONObject(i)
                val entity = CredentialEntity(
                    id = item.optLong("id", 0L),
                    title = item.getString("title"),
                    username = item.getString("username"),
                    passwordCipher = item.getString("passwordCipher"),
                    passwordIv = item.getString("passwordIv"),
                    url = item.optString("url").takeIf { it.isNotEmpty() },
                    notesCipher = item.optString("notesCipher").takeIf { it.isNotEmpty() },
                    notesIv = item.optString("notesIv").takeIf { it.isNotEmpty() },
                    categoryId = if (item.has("categoryId") && !item.isNull("categoryId")) item.getLong("categoryId") else null,
                    favorite = item.optBoolean("favorite", false),
                    createdAt = item.optLong("createdAt", System.currentTimeMillis()),
                    updatedAt = item.optLong("updatedAt", System.currentTimeMillis())
                )
                credentialDao.upsert(entity)
            }
        }
    }

    private fun CredentialEntity.toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("title", title)
        put("username", username)
        put("passwordCipher", passwordCipher)
        put("passwordIv", passwordIv)
        put("url", url ?: "")
        put("notesCipher", notesCipher ?: "")
        put("notesIv", notesIv ?: "")
        put("categoryId", categoryId)
        put("favorite", favorite)
        put("createdAt", createdAt)
        put("updatedAt", updatedAt)
    }

    private fun CategoryEntity.toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("name", name)
        put("iconRes", iconRes)
    }

    private fun deriveKey(password: String, salt: ByteArray): ByteArray =
        SCrypt.scrypt(password.toByteArray(Charsets.UTF_8), salt, N, R, P, KEY_SIZE)

    private fun encrypt(plain: ByteArray, key: ByteArray, iv: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val secretKey = SecretKeySpec(key, "AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(GCM_TAG_LENGTH, iv))
        return cipher.doFinal(plain)
    }

    private fun decrypt(cipherText: ByteArray, key: ByteArray, iv: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val secretKey = SecretKeySpec(key, "AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(GCM_TAG_LENGTH, iv))
        return cipher.doFinal(cipherText)
    }

    private companion object {
        const val EXPORT_VERSION = 1
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val GCM_TAG_LENGTH = 128
        const val SALT_SIZE = 16
        const val IV_SIZE = 12
        const val KEY_SIZE = 32
        const val N = 1 shl 14
        const val R = 8
        const val P = 1
    }
}
