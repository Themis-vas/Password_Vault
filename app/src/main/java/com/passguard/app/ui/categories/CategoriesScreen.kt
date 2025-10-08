package com.passguard.app.ui.categories

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.passguard.app.R

@Composable
fun CategoriesRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CategoriesViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    CategoriesScreen(
        categories = categories,
        onAddCategory = viewModel::addCategory,
        onDeleteCategory = viewModel::removeCategory,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun CategoriesScreen(
    categories: List<com.passguard.app.domain.model.Category>,
    onAddCategory: (String, String) -> Unit,
    onDeleteCategory: (Long) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var icon by remember { mutableStateOf("ic_category_others") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_categories)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.field_category_name)) }
            )
            OutlinedTextField(
                value = icon,
                onValueChange = { icon = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.field_category_icon)) }
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = {
                onAddCategory(name, icon)
                name = ""
            }) {
                Text(text = stringResource(R.string.action_add))
            }
            Spacer(modifier = Modifier.height(24.dp))
            LazyColumn {
                items(categories) { category ->
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = category.name)
                            Text(text = category.iconRes, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { onDeleteCategory(category.id) }) {
                            Icon(imageVector = Icons.Filled.Delete, contentDescription = stringResource(R.string.action_delete))
                        }
                    }
                }
            }
        }
    }
}
