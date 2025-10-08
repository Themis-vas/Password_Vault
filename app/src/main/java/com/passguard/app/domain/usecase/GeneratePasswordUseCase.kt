package com.passguard.app.domain.usecase

import com.passguard.app.domain.model.PasswordOptions
import javax.inject.Inject
import kotlin.random.Random

class GeneratePasswordUseCase @Inject constructor() {
    operator fun invoke(options: PasswordOptions): String {
        val lower = "abcdefghjkmnpqrstuvwxyz"
        val upper = "ABCDEFGHJKMNPQRSTUVWXYZ"
        val digits = "23456789"
        val symbols = "!@#\$%^&*()-_=+[]{};:,.?/"

        val characterSets = mutableListOf<String>()
        if (options.includeLowercase) characterSets += if (options.avoidAmbiguous) lower else "abcdefghijklmnopqrstuvwxyz"
        if (options.includeUppercase) characterSets += if (options.avoidAmbiguous) upper else "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        if (options.includeDigits) characterSets += if (options.avoidAmbiguous) digits else "0123456789"
        if (options.includeSymbols) characterSets += symbols

        require(characterSets.isNotEmpty()) { "No character sets selected" }
        require(options.length in 4..64) { "Invalid password length" }

        val random = Random(System.currentTimeMillis())
        val passwordChars = mutableListOf<Char>()

        characterSets.forEach { set ->
            passwordChars += set[random.nextInt(set.length)]
        }

        val allChars = characterSets.joinToString("")
        while (passwordChars.size < options.length) {
            passwordChars += allChars[random.nextInt(allChars.length)]
        }

        passwordChars.shuffle(random)
        return passwordChars.joinToString("")
    }
}
