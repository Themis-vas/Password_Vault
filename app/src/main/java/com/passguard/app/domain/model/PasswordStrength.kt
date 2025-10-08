package com.passguard.app.domain.model

enum class PasswordStrength(val score: Int) {
    VERY_WEAK(0),
    WEAK(1),
    MEDIUM(2),
    STRONG(3),
    VERY_STRONG(4);
}
