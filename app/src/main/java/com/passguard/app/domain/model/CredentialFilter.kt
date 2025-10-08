package com.passguard.app.domain.model

data class CredentialFilter(
    val categoryId: Long? = null,
    val favoritesOnly: Boolean = false,
    val recentOnly: Boolean = false
)
