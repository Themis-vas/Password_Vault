package com.passguard.app.domain.usecase

import com.passguard.app.domain.model.Credential
import com.passguard.app.domain.model.CredentialFilter
import com.passguard.app.domain.repository.CredentialRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchCredentialsUseCase @Inject constructor(
    private val credentialRepository: CredentialRepository
) {
    operator fun invoke(query: String, filter: CredentialFilter): Flow<List<Credential>> =
        credentialRepository.search(query, filter)
}
