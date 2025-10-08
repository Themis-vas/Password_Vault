package com.passguard.app.domain.usecase

import com.passguard.app.domain.repository.CredentialRepository
import javax.inject.Inject

class DeleteCredentialUseCase @Inject constructor(
    private val credentialRepository: CredentialRepository
) {
    suspend operator fun invoke(id: Long) = credentialRepository.delete(id)
}
