package com.passguard.app.domain.usecase

import com.passguard.app.domain.repository.LockRepository
import javax.inject.Inject

class MarkUnlockedUseCase @Inject constructor(
    private val lockRepository: LockRepository
) {
    suspend operator fun invoke() = lockRepository.markUnlocked()
}
