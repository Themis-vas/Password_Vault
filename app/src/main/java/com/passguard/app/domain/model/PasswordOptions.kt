package com.passguard.app.domain.model

data class PasswordOptions(
    val length: Int = 16,
    val includeLowercase: Boolean = true,
    val includeUppercase: Boolean = true,
    val includeDigits: Boolean = true,
    val includeSymbols: Boolean = true,
    val avoidAmbiguous: Boolean = true
)
