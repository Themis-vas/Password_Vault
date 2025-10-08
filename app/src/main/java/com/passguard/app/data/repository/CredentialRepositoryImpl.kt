package com.passguard.app.data.repository

import com.passguard.app.data.local.dao.CredentialDao
import com.passguard.app.data.mapper.toDomain
import com.passguard.app.data.mapper.toEntity
import com.passguard.app.domain.model.Credential
import com.passguard.app.domain.model.CredentialFilter
import com.passguard.app.domain.repository.CredentialRepository
import com.passguard.app.core.crypto.CryptoManager
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

@Singleton
class CredentialRepositoryImpl @Inject constructor(
    private val credentialDao: CredentialDao,
    private val cryptoManager: CryptoManager,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CredentialRepository {

    override fun observeCredentials(): Flow<List<Credential>> =
        credentialDao.observeAll()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)

    override fun observeFavorites(): Flow<List<Credential>> =
        credentialDao.observeFavorites()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)

    override fun search(query: String, filter: CredentialFilter): Flow<List<Credential>> =
        observeCredentials().map { credentials ->
            val normalized = query.trim().lowercase()
            credentials.filter { credential ->
                val matchesQuery = if (normalized.isEmpty()) {
                    true
                } else {
                    val notesText = credential.notes?.let { payload ->
                        runCatching { cryptoManager.decrypt(payload) }.getOrDefault("")
                    } ?: ""
                    listOfNotNull(
                        credential.title,
                        credential.username,
                        credential.url,
                        notesText
                    ).any { field -> field.lowercase().contains(normalized) }
                }

                val matchesCategory = filter.categoryId?.let { it == credential.categoryId } ?: true
                val matchesFavorite = if (filter.favoritesOnly) credential.favorite else true
                val matchesRecent = if (filter.recentOnly) {
                    val current = System.currentTimeMillis()
                    current - credential.updatedAt <= RECENT_WINDOW_MILLIS
                } else {
                    true
                }

                matchesQuery && matchesCategory && matchesFavorite && matchesRecent
            }.sortedWith(
                compareByDescending<Credential> { it.favorite }
                    .thenBy { it.title.lowercase() }
            )
        }.flowOn(ioDispatcher)

    override suspend fun getCredential(id: Long): Credential? = withContext(ioDispatcher) {
        credentialDao.getById(id)?.toDomain()
    }

    override suspend fun upsert(credential: Credential): Long = withContext(ioDispatcher) {
        credentialDao.upsert(credential.toEntity())
    }

    override suspend fun delete(id: Long) = withContext(ioDispatcher) {
        credentialDao.delete(id)
    }

    override suspend fun updateFavorite(id: Long, favorite: Boolean) = withContext(ioDispatcher) {
        credentialDao.updateFavorite(id, favorite, System.currentTimeMillis())
    }

    private companion object {
        const val RECENT_WINDOW_MILLIS: Long = 7 * 24 * 60 * 60 * 1000L
    }
}
