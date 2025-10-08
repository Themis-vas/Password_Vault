package com.passguard.app.domain.model

data class Credential(
    val id: Long = 0L,
    val title: String,
    val username: String,
    val password: EncryptedPayload,
    val url: String?,
    val notes: EncryptedPayload?,
    val categoryId: Long?,
    val favorite: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)
