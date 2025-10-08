package com.passguard.app.ui.importexport

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
fun ImportExportRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ImportExportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ImportExportScreen(
        state = uiState,
        onBack = onBack,
        onImport = viewModel::import,
        onExport = viewModel::export,
        modifier = modifier
    )
}

@Composable
fun ImportExportScreen(
    state: ImportExportUiState,
    onBack: () -> Unit,
    onImport: (Uri, String) -> Unit,
    onExport: (Uri, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var password by remember { mutableStateOf("") }
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")) { uri ->
        if (uri != null) {
            onExport(uri, password)
        }
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            onImport(uri, password)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.title_import_export)) },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = stringResource(R.string.import_export_description))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.field_master_password)) }
            )
            Button(onClick = { exportLauncher.launch("passguard_backup.pgvault") }, enabled = password.isNotBlank()) {
                Text(text = stringResource(R.string.action_export))
            }
            Button(onClick = { importLauncher.launch(arrayOf("application/octet-stream")) }, enabled = password.isNotBlank()) {
                Text(text = stringResource(R.string.action_import))
            }
            state.message?.let {
                val localized = when (it) {
                    "import_success" -> stringResource(R.string.import_success)
                    "export_success" -> stringResource(R.string.export_success)
                    else -> it
                }
                Text(text = localized)
            }
        }
    }
}
