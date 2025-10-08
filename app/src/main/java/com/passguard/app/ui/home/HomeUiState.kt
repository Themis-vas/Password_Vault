package com.passguard.app.ui.home

import com.passguard.app.domain.model.Category
import com.passguard.app.domain.model.Credential
import com.passguard.app.domain.model.CredentialFilter

data class HomeUiState(
    val credentials: List<Credential> = emptyList(),
    val favorites: List<Credential> = emptyList(),
    val categories: List<Category> = emptyList(),
    val searchQuery: String = "",
    val filter: CredentialFilter = CredentialFilter(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
