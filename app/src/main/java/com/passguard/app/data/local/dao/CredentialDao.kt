package com.passguard.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.passguard.app.data.local.entity.CredentialEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CredentialDao {
    @Query("SELECT * FROM credentials ORDER BY title COLLATE NOCASE")
    fun observeAll(): Flow<List<CredentialEntity>>

    @Query("SELECT * FROM credentials WHERE favorite = 1 ORDER BY updated_at DESC")
    fun observeFavorites(): Flow<List<CredentialEntity>>

    @Query("SELECT * FROM credentials WHERE id = :id")
    suspend fun getById(id: Long): CredentialEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CredentialEntity): Long

    @Query("DELETE FROM credentials WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM credentials")
    suspend fun deleteAll()

    @Query("UPDATE credentials SET favorite = :favorite, updated_at = :timestamp WHERE id = :id")
    suspend fun updateFavorite(id: Long, favorite: Boolean, timestamp: Long)

    @RawQuery(observedEntities = [CredentialEntity::class])
    fun search(query: SupportSQLiteQuery): Flow<List<CredentialEntity>>

    @Query("SELECT * FROM credentials")
    suspend fun getAllOnce(): List<CredentialEntity>
}
