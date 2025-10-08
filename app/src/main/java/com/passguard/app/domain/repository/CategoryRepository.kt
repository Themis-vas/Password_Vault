package com.passguard.app.domain.repository

import com.passguard.app.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun observeCategories(): Flow<List<Category>>
    suspend fun insertDefaults()
    suspend fun upsert(category: Category): Long
    suspend fun delete(id: Long)
    suspend fun getCategory(id: Long): Category?
}
