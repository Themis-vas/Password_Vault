package com.passguard.app.domain.usecase

import com.passguard.app.domain.model.PasswordStrength
import javax.inject.Inject

class EvaluatePasswordStrengthUseCase @Inject constructor() {
    operator fun invoke(password: String): PasswordStrength {
        var score = 0
        if (password.length >= 8) score++
        if (password.length >= 12) score++
        if (password.any { it.isDigit() }) score++
        if (password.any { !it.isLetterOrDigit() }) score++
        return when (score) {
            0 -> PasswordStrength.VERY_WEAK
            1 -> PasswordStrength.WEAK
            2 -> PasswordStrength.MEDIUM
            3 -> PasswordStrength.STRONG
            else -> PasswordStrength.VERY_STRONG
        }
    }
}
