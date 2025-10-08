package com.passguard.app.ui.credential

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.passguard.app.R
import com.passguard.app.domain.model.PasswordStrength

@Composable
fun CredentialEditorRoute(
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CredentialEditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState.saved) {
        if (uiState.saved) onDone()
    }
    CredentialEditorScreen(
        state = uiState,
        onTitleChange = viewModel::onTitleChange,
        onUsernameChange = viewModel::onUsernameChange,
        onPasswordChange = viewModel::onPasswordChange,
        onUrlChange = viewModel::onUrlChange,
        onNotesChange = viewModel::onNotesChange,
        onCategoryChange = viewModel::onCategoryChange,
        onFavoriteChange = viewModel::onFavoriteChange,
        onGeneratePassword = viewModel::generatePassword,
        onSave = viewModel::save,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CredentialEditorScreen(
    state: CredentialEditorUiState,
    onTitleChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onUrlChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onCategoryChange: (Long?) -> Unit,
    onFavoriteChange: (Boolean) -> Unit,
    onGeneratePassword: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = if (state.isEditMode) stringResource(R.string.title_edit_credential) else stringResource(R.string.title_add_credential))
                },
                actions = {
                    IconButton(onClick = onSave) {
                        Icon(imageVector = Icons.Filled.Save, contentDescription = stringResource(R.string.action_save))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.title,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.field_title)) }
            )
            OutlinedTextField(
                value = state.username,
                onValueChange = onUsernameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.field_username)) }
            )
            OutlinedTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.field_password)) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    Row {
                        Button(onClick = onGeneratePassword) {
                            Text(stringResource(R.string.action_generate))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = { passwordVisible = !passwordVisible }) {
                            Text(if (passwordVisible) stringResource(R.string.action_hide) else stringResource(R.string.action_reveal))
                        }
                    }
                }
            )
            PasswordStrengthBar(strength = state.passwordStrength)
            OutlinedTextField(
                value = state.url,
                onValueChange = onUrlChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.field_url)) }
            )
            OutlinedTextField(
                value = state.notes,
                onValueChange = onNotesChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                label = { Text(stringResource(R.string.field_notes)) }
            )
            Column {
                Text(text = stringResource(R.string.field_category))
                Spacer(modifier = Modifier.height(4.dp))
                state.categories.forEach { category ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = state.categoryId == category.id,
                            onClick = { onCategoryChange(category.id) }
                        )
                        Text(text = category.name, modifier = Modifier.padding(start = 8.dp))
                    }
                }
                Button(onClick = { onCategoryChange(null) }) {
                    Text(stringResource(R.string.action_clear_category))
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = state.favorite, onCheckedChange = onFavoriteChange)
                Text(text = stringResource(R.string.field_favorite))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onSave, modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(R.string.action_save))
            }
        }
    }
}

@Composable
private fun PasswordStrengthBar(strength: PasswordStrength) {
    val level = when (strength) {
        PasswordStrength.VERY_WEAK -> 0.2f
        PasswordStrength.WEAK -> 0.4f
        PasswordStrength.MEDIUM -> 0.6f
        PasswordStrength.STRONG -> 0.8f
        PasswordStrength.VERY_STRONG -> 1f
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = stringResource(R.string.label_strength, strength.name))
        Slider(value = level, onValueChange = {}, enabled = false)
    }
}
