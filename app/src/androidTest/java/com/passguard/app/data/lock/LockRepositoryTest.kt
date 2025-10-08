package com.passguard.app.data.lock

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.passguard.app.domain.model.LockState
import com.passguard.app.domain.repository.LockRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LockRepositoryTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var lockRepository: LockRepository

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun setPin_unlocksAndLocks() = runBlocking {
        lockRepository.setPin("1234")
        val lockedState = lockRepository.lockState.first()
        assertEquals(LockState.Locked::class, lockedState::class)
        val success = lockRepository.validatePin("1234")
        assertEquals(true, success)
        val unlockedState = lockRepository.lockState.first()
        assertEquals(LockState.Unlocked::class, unlockedState::class)
        lockRepository.lock()
        val relocked = lockRepository.lockState.first()
        assertEquals(LockState.Locked::class, relocked::class)
    }
}
