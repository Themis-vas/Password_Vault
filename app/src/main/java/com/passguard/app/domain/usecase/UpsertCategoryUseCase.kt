package com.passguard.app.domain.usecase

import com.passguard.app.domain.model.Category
import com.passguard.app.domain.repository.CategoryRepository
import javax.inject.Inject

class UpsertCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(category: Category): Long = categoryRepository.upsert(category)
}
