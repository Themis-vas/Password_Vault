package com.passguard.app.domain.repository

import com.passguard.app.domain.model.Credential
import com.passguard.app.domain.model.CredentialFilter
import kotlinx.coroutines.flow.Flow

interface CredentialRepository {
    fun observeCredentials(): Flow<List<Credential>>
    fun observeFavorites(): Flow<List<Credential>>
    fun search(query: String, filter: CredentialFilter): Flow<List<Credential>>
    suspend fun getCredential(id: Long): Credential?
    suspend fun upsert(credential: Credential): Long
    suspend fun delete(id: Long)
    suspend fun updateFavorite(id: Long, favorite: Boolean)
}
