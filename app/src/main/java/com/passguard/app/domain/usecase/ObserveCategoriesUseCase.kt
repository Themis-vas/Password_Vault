package com.passguard.app.domain.usecase

import com.passguard.app.domain.model.Category
import com.passguard.app.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(): Flow<List<Category>> = categoryRepository.observeCategories()
}
