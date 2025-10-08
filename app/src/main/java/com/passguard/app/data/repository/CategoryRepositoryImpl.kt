package com.passguard.app.data.repository

import android.content.Context
import com.passguard.app.R
import com.passguard.app.data.local.dao.CategoryDao
import com.passguard.app.data.mapper.toDomain
import com.passguard.app.data.mapper.toEntity
import com.passguard.app.domain.model.Category
import com.passguard.app.domain.repository.CategoryRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    @ApplicationContext private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CategoryRepository {

    override fun observeCategories(): Flow<List<Category>> =
        categoryDao.observeCategories()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)

    override suspend fun insertDefaults() = withContext(ioDispatcher) {
        if (categoryDao.count() == 0) {
            DEFAULT_CATEGORIES(context).forEach { category ->
                categoryDao.upsert(category.toEntity())
            }
        }
    }

    override suspend fun upsert(category: Category): Long = withContext(ioDispatcher) {
        categoryDao.upsert(category.toEntity())
    }

    override suspend fun delete(id: Long) = withContext(ioDispatcher) {
        categoryDao.delete(id)
    }

    override suspend fun getCategory(id: Long): Category? = withContext(ioDispatcher) {
        categoryDao.getById(id)?.toDomain()
    }

    private companion object {
        fun DEFAULT_CATEGORIES(context: Context): List<Category> = listOf(
            Category(name = context.getString(R.string.category_social), iconRes = "ic_category_social"),
            Category(name = context.getString(R.string.category_banks), iconRes = "ic_category_banks"),
            Category(name = context.getString(R.string.category_shopping), iconRes = "ic_category_shopping"),
            Category(name = context.getString(R.string.category_work), iconRes = "ic_category_work"),
            Category(name = context.getString(R.string.category_others), iconRes = "ic_category_others")
        )
    }
}
