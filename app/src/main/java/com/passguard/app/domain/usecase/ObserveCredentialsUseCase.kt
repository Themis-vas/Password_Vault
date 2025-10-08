package com.passguard.app.domain.usecase

import com.passguard.app.domain.model.Credential
import com.passguard.app.domain.repository.CredentialRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCredentialsUseCase @Inject constructor(
    private val credentialRepository: CredentialRepository
) {
    operator fun invoke(): Flow<List<Credential>> = credentialRepository.observeCredentials()
}
