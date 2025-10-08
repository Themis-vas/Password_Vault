package com.passguard.app.di

import com.passguard.app.data.lock.LockRepositoryImpl
import com.passguard.app.data.repository.CategoryRepositoryImpl
import com.passguard.app.data.repository.CredentialRepositoryImpl
import com.passguard.app.data.settings.SettingsRepositoryImpl
import com.passguard.app.data.transfer.VaultTransferRepositoryImpl
import com.passguard.app.domain.repository.CategoryRepository
import com.passguard.app.domain.repository.CredentialRepository
import com.passguard.app.domain.repository.LockRepository
import com.passguard.app.domain.repository.SettingsRepository
import com.passguard.app.domain.repository.VaultTransferRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCredentialRepository(impl: CredentialRepositoryImpl): CredentialRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindLockRepository(impl: LockRepositoryImpl): LockRepository

    @Binds
    @Singleton
    abstract fun bindVaultTransferRepository(impl: VaultTransferRepositoryImpl): VaultTransferRepository
}
