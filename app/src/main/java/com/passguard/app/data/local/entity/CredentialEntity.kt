package com.passguard.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "credentials")
data class CredentialEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val username: String,
    @ColumnInfo(name = "password_cipher")
    val passwordCipher: String,
    @ColumnInfo(name = "password_iv")
    val passwordIv: String,
    val url: String?,
    @ColumnInfo(name = "notes_cipher")
    val notesCipher: String?,
    @ColumnInfo(name = "notes_iv")
    val notesIv: String?,
    @ColumnInfo(name = "category_id")
    val categoryId: Long?,
    val favorite: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)
