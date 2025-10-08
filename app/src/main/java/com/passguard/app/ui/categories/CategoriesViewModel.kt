package com.passguard.app.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.passguard.app.domain.model.Category
import com.passguard.app.domain.usecase.DeleteCategoryUseCase
import com.passguard.app.domain.usecase.ObserveCategoriesUseCase
import com.passguard.app.domain.usecase.UpsertCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    observeCategoriesUseCase: ObserveCategoriesUseCase,
    private val upsertCategoryUseCase: UpsertCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase
) : ViewModel() {

    val categories: StateFlow<List<Category>> = observeCategoriesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun addCategory(name: String, icon: String) {
        if (name.isBlank()) {
            _error.value = "invalid"
            return
        }
        viewModelScope.launch {
            upsertCategoryUseCase(Category(name = name, iconRes = icon))
        }
    }

    fun removeCategory(id: Long) {
        viewModelScope.launch { deleteCategoryUseCase(id) }
    }
}
