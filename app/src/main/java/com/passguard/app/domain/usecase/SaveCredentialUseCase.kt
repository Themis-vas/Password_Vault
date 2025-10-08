package com.passguard.app.domain.usecase

import com.passguard.app.domain.model.Credential
import com.passguard.app.domain.repository.CredentialRepository
import javax.inject.Inject

class SaveCredentialUseCase @Inject constructor(
    private val credentialRepository: CredentialRepository
) {
    suspend operator fun invoke(credential: Credential): Long =
        credentialRepository.upsert(credential)
}
