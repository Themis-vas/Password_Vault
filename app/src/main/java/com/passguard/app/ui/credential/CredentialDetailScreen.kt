package com.passguard.app.ui.credential

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.passguard.app.R

@Composable
fun CredentialDetailRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CredentialDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CredentialDetailScreen(
        state = uiState,
        onBack = onBack,
        onTogglePassword = viewModel::togglePasswordVisibility,
        onCopyPassword = viewModel::copyPassword,
        modifier = modifier
    )
}

@Composable
fun CredentialDetailScreen(
    state: CredentialDetailUiState,
    onBack: () -> Unit,
    onTogglePassword: () -> Unit,
    onCopyPassword: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.title.ifEmpty { stringResource(R.string.title_credential_details) },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
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
                .padding(horizontal = 16.dp)
        ) {
            Text(text = stringResource(R.string.field_username), style = MaterialTheme.typography.labelMedium)
            Text(text = state.username, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stringResource(R.string.field_password), style = MaterialTheme.typography.labelMedium)
            RowWithActions(
                value = if (state.isPasswordVisible) state.password else stringResource(R.string.value_hidden),
                onToggle = onTogglePassword,
                onCopy = onCopyPassword,
                isVisible = state.isPasswordVisible
            )
            Spacer(modifier = Modifier.height(16.dp))
            state.url?.let {
                Text(text = stringResource(R.string.field_url), style = MaterialTheme.typography.labelMedium)
                Text(text = it, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
            }
            if (state.notes.isNotBlank()) {
                Text(text = stringResource(R.string.field_notes), style = MaterialTheme.typography.labelMedium)
                Text(text = state.notes, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
private fun RowWithActions(
    value: String,
    onToggle: () -> Unit,
    onCopy: () -> Unit,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onToggle) {
            Icon(
                imageVector = if (isVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                contentDescription = stringResource(R.string.action_reveal)
            )
        }
        IconButton(onClick = onCopy) {
            Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = stringResource(R.string.action_copy))
        }
    }
}
