package com.passguard.app.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.passguard.app.data.local.dao.CredentialDao
import com.passguard.app.data.local.entity.CredentialEntity
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CredentialDaoTest {

    private lateinit var database: PassGuardDatabase
    private lateinit var dao: CredentialDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PassGuardDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.credentialDao()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndFetchCredential() = runBlocking {
        val entity = CredentialEntity(
            title = "Email",
            username = "user@example.com",
            passwordCipher = "cipher",
            passwordIv = "iv",
            url = "https://example.com",
            notesCipher = null,
            notesIv = null,
            categoryId = null,
            favorite = false,
            createdAt = 1L,
            updatedAt = 1L
        )
        val id = dao.upsert(entity)
        val fetched = dao.getById(id)
        assertNotNull(fetched)
        assertEquals("Email", fetched.title)
    }
}
