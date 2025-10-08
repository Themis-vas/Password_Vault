package com.passguard.app.di

import android.content.Context
import androidx.room.Room
import com.passguard.app.data.local.PassGuardDatabase
import com.passguard.app.data.local.dao.CategoryDao
import com.passguard.app.data.local.dao.CredentialDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context): PassGuardDatabase =
        Room.databaseBuilder(
            context,
            PassGuardDatabase::class.java,
            "passguard.db"
        ).fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideCredentialDao(database: PassGuardDatabase): CredentialDao = database.credentialDao()

    @Provides
    fun provideCategoryDao(database: PassGuardDatabase): CategoryDao = database.categoryDao()
}
