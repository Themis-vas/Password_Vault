package com.passguard.app.domain.model

data class EncryptedPayload(
    val cipherText: String,
    val initializationVector: String
)
