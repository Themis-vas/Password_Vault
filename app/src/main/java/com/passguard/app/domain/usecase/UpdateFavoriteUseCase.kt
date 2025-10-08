package com.passguard.app.domain.usecase

import com.passguard.app.domain.repository.CredentialRepository
import javax.inject.Inject

class UpdateFavoriteUseCase @Inject constructor(
    private val credentialRepository: CredentialRepository
) {
    suspend operator fun invoke(id: Long, favorite: Boolean) =
        credentialRepository.updateFavorite(id, favorite)
}
