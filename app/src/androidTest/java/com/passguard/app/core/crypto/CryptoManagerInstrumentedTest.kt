package com.passguard.app.core.crypto

import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import kotlin.test.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CryptoManagerInstrumentedTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var cryptoManager: CryptoManager

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun encryptDecrypt_roundTrip() {
        val secret = "passguard-secret"
        val encrypted = cryptoManager.encrypt(secret)
        val decrypted = cryptoManager.decrypt(encrypted)
        assertEquals(secret, decrypted)
    }
}
