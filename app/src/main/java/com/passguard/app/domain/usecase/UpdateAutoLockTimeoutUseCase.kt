package com.passguard.app.domain.usecase

import com.passguard.app.domain.repository.LockRepository
import javax.inject.Inject

class UpdateAutoLockTimeoutUseCase @Inject constructor(
    private val lockRepository: LockRepository
) {
    suspend operator fun invoke(minutes: Int) = lockRepository.updateTimeout(minutes)
}
