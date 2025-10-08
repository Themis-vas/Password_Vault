package com.passguard.app.domain.usecase

import com.passguard.app.domain.model.PasswordOptions
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GeneratePasswordUseCaseTest {

    private val useCase = GeneratePasswordUseCase()

    @Test
    fun generatesPasswordWithRequestedLength() {
        val password = useCase(PasswordOptions(length = 20))
        assertEquals(20, password.length)
    }

    @Test
    fun generatesPasswordWithoutAmbiguousCharacters() {
        val password = useCase(PasswordOptions(length = 32, avoidAmbiguous = true))
        assertFalse(password.contains('0'))
        assertFalse(password.contains('O'))
    }

    @Test
    fun generatesPasswordWithSymbolsWhenRequested() {
        val password = useCase(PasswordOptions(includeSymbols = true))
        assertTrue(password.any { !it.isLetterOrDigit() })
    }
}
