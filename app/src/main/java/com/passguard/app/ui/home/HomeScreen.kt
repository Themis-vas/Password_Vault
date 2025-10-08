package com.passguard.app.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.passguard.app.R
import com.passguard.app.domain.model.Credential
import com.passguard.app.domain.model.CredentialFilter

@Composable
fun HomeRoute(
    onCredentialClick: (Long) -> Unit,
    onAddCredential: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenCategories: () -> Unit,
    isExpanded: Boolean,
    onOpenGenerator: () -> Unit,
    onOpenImportExport: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        state = uiState,
        onCredentialClick = onCredentialClick,
        onSearchChange = viewModel::onSearchQueryChange,
        onFilterChange = viewModel::onFilterChange,
        onClearFilters = viewModel::clearFilters,
        onToggleFavorite = viewModel::toggleFavorite,
        onDeleteCredential = viewModel::deleteCredential,
        onAddCredential = onAddCredential,
        onOpenSettings = onOpenSettings,
        onOpenCategories = onOpenCategories,
        isExpanded = isExpanded,
        onOpenGenerator = onOpenGenerator,
        onOpenImportExport = onOpenImportExport,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    state: HomeUiState,
    onCredentialClick: (Long) -> Unit,
    onSearchChange: (String) -> Unit,
    onFilterChange: (CredentialFilter) -> Unit,
    onClearFilters: () -> Unit,
    onToggleFavorite: (Long, Boolean) -> Unit,
    onDeleteCredential: (Long) -> Unit,
    onAddCredential: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenCategories: () -> Unit,
    isExpanded: Boolean,
    onOpenGenerator: () -> Unit,
    onOpenImportExport: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilters by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                navigationIcon = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(imageVector = Icons.Outlined.Security, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(imageVector = Icons.Filled.FilterList, contentDescription = stringResource(R.string.action_filter))
                    }
                    IconButton(onClick = onOpenCategories) {
                        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = stringResource(R.string.title_categories))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddCredential) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = stringResource(R.string.action_add))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SearchBar(
                value = state.searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            AnimatedVisibility(visible = showFilters) {
                FilterSection(
                    filter = state.filter,
                    onFilterChange = onFilterChange,
                    onClearFilters = onClearFilters,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(onClick = onOpenGenerator, label = { Text(stringResource(R.string.title_password_generator)) })
                AssistChip(onClick = onOpenImportExport, label = { Text(stringResource(R.string.title_import_export)) })
            }

            if (state.credentials.isEmpty()) {
                EmptyState(modifier = Modifier.fillMaxSize())
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(if (isExpanded) 20.dp else 12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
                ) {
                    items(state.credentials, key = { it.id }) { credential ->
                        CredentialCard(
                            credential = credential,
                            onClick = { onCredentialClick(credential.id) },
                            onToggleFavorite = { onToggleFavorite(credential.id, !credential.favorite) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        placeholder = { Text(stringResource(id = R.string.hint_search_credentials)) }
    )
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(id = R.string.empty_credentials_title), style = MaterialTheme.typography.titleLarge)
        Text(
            text = stringResource(id = R.string.empty_credentials_message),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun FilterSection(
    filter: CredentialFilter,
    onFilterChange: (CredentialFilter) -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(id = R.string.label_filters), style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.weight(1f))
            AssistChip(
                onClick = onClearFilters,
                label = { Text(stringResource(id = R.string.action_clear)) },
                colors = AssistChipDefaults.assistChipColors(containerColor = Color.Transparent)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(
                onClick = { onFilterChange(filter.copy(favoritesOnly = !filter.favoritesOnly)) },
                label = { Text(stringResource(id = R.string.filter_favorites)) },
                leadingIcon = {
                    Icon(
                        imageVector = if (filter.favoritesOnly) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = null
                    )
                }
            )
            AssistChip(
                onClick = { onFilterChange(filter.copy(recentOnly = !filter.recentOnly)) },
                label = { Text(stringResource(id = R.string.filter_recent)) }
            )
        }
    }
}

@Composable
private fun CredentialCard(
    credential: Credential,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = credential.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                Text(
                    text = credential.username,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                credential.url?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (credential.favorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = stringResource(id = R.string.action_toggle_favorite),
                    tint = if (credential.favorite) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
