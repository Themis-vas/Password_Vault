package com.passguard.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.passguard.app.domain.model.CredentialFilter
import com.passguard.app.domain.usecase.DeleteCredentialUseCase
import com.passguard.app.domain.usecase.ObserveCategoriesUseCase
import com.passguard.app.domain.usecase.ObserveCredentialsUseCase
import com.passguard.app.domain.usecase.ObserveFavoritesUseCase
import com.passguard.app.domain.usecase.SearchCredentialsUseCase
import com.passguard.app.domain.usecase.UpdateFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeCredentialsUseCase: ObserveCredentialsUseCase,
    observeFavoritesUseCase: ObserveFavoritesUseCase,
    observeCategoriesUseCase: ObserveCategoriesUseCase,
    private val searchCredentialsUseCase: SearchCredentialsUseCase,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
    private val deleteCredentialUseCase: DeleteCredentialUseCase
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val filterState = MutableStateFlow(CredentialFilter())

    private val filteredCredentials = searchQuery
        .combine(filterState) { query, filter -> query to filter }
        .flatMapLatest { (query, filter) ->
            if (query.isBlank() && filter == CredentialFilter()) {
                observeCredentialsUseCase()
            } else {
                searchCredentialsUseCase(query, filter)
            }
        }

    val uiState: StateFlow<HomeUiState> = combine(
        filteredCredentials,
        observeFavoritesUseCase(),
        observeCategoriesUseCase(),
        searchQuery,
        filterState
    ) { credentials, favorites, categories, query, filter ->
        HomeUiState(
            credentials = credentials,
            favorites = favorites,
            categories = categories,
            searchQuery = query,
            filter = filter,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    fun onFilterChange(filter: CredentialFilter) {
        filterState.value = filter
    }

    fun clearFilters() {
        filterState.value = CredentialFilter()
        searchQuery.value = ""
    }

    fun toggleFavorite(id: Long, favorite: Boolean) {
        viewModelScope.launch { updateFavoriteUseCase(id, favorite) }
    }

    fun deleteCredential(id: Long) {
        viewModelScope.launch { deleteCredentialUseCase(id) }
    }
}
