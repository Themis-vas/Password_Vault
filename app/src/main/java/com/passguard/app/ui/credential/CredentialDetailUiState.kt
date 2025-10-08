package com.passguard.app.ui.credential

data class CredentialDetailUiState(
    val title: String = "",
    val username: String = "",
    val password: String = "",
    val url: String? = null,
    val notes: String = "",
    val categoryName: String? = null,
    val favorite: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)
