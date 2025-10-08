package com.passguard.app.domain.usecase

import com.passguard.app.core.crypto.CryptoManager
import com.passguard.app.domain.model.EncryptedPayload
import javax.inject.Inject

class EncryptTextUseCase @Inject constructor(
    private val cryptoManager: CryptoManager
) {
    operator fun invoke(plainText: String): EncryptedPayload = cryptoManager.encrypt(plainText)
}
