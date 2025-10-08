package com.passguard.app.domain.usecase

import android.net.Uri
import com.passguard.app.domain.repository.VaultTransferRepository
import javax.inject.Inject

class ImportVaultUseCase @Inject constructor(
    private val transferRepository: VaultTransferRepository
) {
    suspend operator fun invoke(uri: Uri, password: String) {
        transferRepository.importVault(uri, password)
    }
}
