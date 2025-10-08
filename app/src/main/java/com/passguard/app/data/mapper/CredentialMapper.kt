package com.passguard.app.data.mapper

import com.passguard.app.data.local.entity.CredentialEntity
import com.passguard.app.domain.model.Credential
import com.passguard.app.domain.model.EncryptedPayload

fun CredentialEntity.toDomain(): Credential = Credential(
    id = id,
    title = title,
    username = username,
    password = EncryptedPayload(passwordCipher, passwordIv),
    url = url,
    notes = notesCipher?.let { cipher ->
        val iv = notesIv ?: ""
        EncryptedPayload(cipher, iv)
    },
    categoryId = categoryId,
    favorite = favorite,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Credential.toEntity(): CredentialEntity = CredentialEntity(
    id = id,
    title = title,
    username = username,
    passwordCipher = password.cipherText,
    passwordIv = password.initializationVector,
    url = url,
    notesCipher = notes?.cipherText,
    notesIv = notes?.initializationVector,
    categoryId = categoryId,
    favorite = favorite,
    createdAt = createdAt,
    updatedAt = updatedAt
)
