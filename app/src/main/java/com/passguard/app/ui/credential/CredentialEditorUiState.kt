package com.passguard.app.ui.credential

import com.passguard.app.domain.model.Category
import com.passguard.app.domain.model.PasswordStrength

data class CredentialEditorUiState(
    val title: String = "",
    val username: String = "",
    val password: String = "",
    val url: String = "",
    val notes: String = "",
    val categoryId: Long? = null,
    val favorite: Boolean = false,
    val categories: List<Category> = emptyList(),
    val isSaving: Boolean = false,
    val error: String? = null,
    val passwordStrength: PasswordStrength = PasswordStrength.VERY_WEAK,
    val isEditMode: Boolean = false,
    val saved: Boolean = false
)
