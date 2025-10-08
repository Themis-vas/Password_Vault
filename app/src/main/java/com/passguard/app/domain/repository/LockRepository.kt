package com.passguard.app.domain.repository

import com.passguard.app.domain.model.LockState
import kotlinx.coroutines.flow.Flow

interface LockRepository {
    val lockState: Flow<LockState>
    suspend fun setPin(pin: String)
    suspend fun clearPin()
    suspend fun validatePin(pin: String): Boolean
    suspend fun unlock()
    suspend fun markUnlocked()
    suspend fun lock()
    suspend fun shouldAutoLock(): Boolean
    suspend fun updateTimeout(minutes: Int)
}
