package com.passguard.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.passguard.app.data.local.dao.CategoryDao
import com.passguard.app.data.local.dao.CredentialDao
import com.passguard.app.data.local.entity.CategoryEntity
import com.passguard.app.data.local.entity.CredentialEntity

@Database(
    entities = [CredentialEntity::class, CategoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PassGuardDatabase : RoomDatabase() {
    abstract fun credentialDao(): CredentialDao
    abstract fun categoryDao(): CategoryDao
}
