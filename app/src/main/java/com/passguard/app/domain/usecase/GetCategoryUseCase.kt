package com.passguard.app.domain.usecase

import com.passguard.app.domain.model.Category
import com.passguard.app.domain.repository.CategoryRepository
import javax.inject.Inject

class GetCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(id: Long): Category? = categoryRepository.getCategory(id)
}
