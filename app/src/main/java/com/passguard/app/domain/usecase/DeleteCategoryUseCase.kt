package com.passguard.app.domain.usecase

import com.passguard.app.domain.repository.CategoryRepository
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(id: Long) = categoryRepository.delete(id)
}
