package com.passguard.app.domain.repository

import android.net.Uri

interface VaultTransferRepository {
    suspend fun exportVault(uri: Uri, password: String)
    suspend fun importVault(uri: Uri, password: String)
}
