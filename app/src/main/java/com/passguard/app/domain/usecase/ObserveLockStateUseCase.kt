package com.passguard.app.domain.usecase

import com.passguard.app.domain.model.LockState
import com.passguard.app.domain.repository.LockRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveLockStateUseCase @Inject constructor(
    private val lockRepository: LockRepository
) {
    operator fun invoke(): Flow<LockState> = lockRepository.lockState
}
