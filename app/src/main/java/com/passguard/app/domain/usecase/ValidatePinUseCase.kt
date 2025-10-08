package com.passguard.app.domain.usecase

import com.passguard.app.domain.repository.LockRepository
import javax.inject.Inject

class ValidatePinUseCase @Inject constructor(
    private val lockRepository: LockRepository
) {
    suspend operator fun invoke(pin: String): Boolean = lockRepository.validatePin(pin)
}
