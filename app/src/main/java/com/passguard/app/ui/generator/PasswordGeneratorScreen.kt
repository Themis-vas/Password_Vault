package com.passguard.app.ui.generator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import com.passguard.app.domain.model.PasswordOptions

@Composable
fun PasswordGeneratorRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PasswordGeneratorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    PasswordGeneratorScreen(
        state = uiState,
        onBack = onBack,
        onOptionsChange = viewModel::updateOptions,
        onCopy = viewModel::copyToClipboard,
        onRegenerate = viewModel::generate,
        modifier = modifier
    )
}

@Composable
fun PasswordGeneratorScreen(
    state: PasswordGeneratorUiState,
    onBack: () -> Unit,
    onOptionsChange: (PasswordOptions) -> Unit,
    onCopy: () -> Unit,
    onRegenerate: () -> Unit,
    modifier: Modifier = Modifier
) {
    var length by remember { mutableStateOf(state.options.length.toString()) }
    var includeLower by remember { mutableStateOf(state.options.includeLowercase) }
    var includeUpper by remember { mutableStateOf(state.options.includeUppercase) }
    var includeDigits by remember { mutableStateOf(state.options.includeDigits) }
    var includeSymbols by remember { mutableStateOf(state.options.includeSymbols) }
    var avoidAmbiguous by remember { mutableStateOf(state.options.avoidAmbiguous) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_password_generator)) },
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
            Text(text = stringResource(R.string.label_generated_password, state.password))
            OutlinedTextField(
                value = length,
                onValueChange = {
                    length = it.filter { char -> char.isDigit() }
                    length.toIntOrNull()?.let { len ->
                        onOptionsChange(
                            PasswordOptions(
                                length = len,
                                includeLowercase = includeLower,
                                includeUppercase = includeUpper,
                                includeDigits = includeDigits,
                                includeSymbols = includeSymbols,
                                avoidAmbiguous = avoidAmbiguous
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.field_password_length)) }
            )
            OptionRow(title = stringResource(R.string.option_lowercase), checked = includeLower) {
                includeLower = it
                onOptionsChange(state.options.copy(includeLowercase = it))
            }
            OptionRow(title = stringResource(R.string.option_uppercase), checked = includeUpper) {
                includeUpper = it
                onOptionsChange(state.options.copy(includeUppercase = it))
            }
            OptionRow(title = stringResource(R.string.option_digits), checked = includeDigits) {
                includeDigits = it
                onOptionsChange(state.options.copy(includeDigits = it))
            }
            OptionRow(title = stringResource(R.string.option_symbols), checked = includeSymbols) {
                includeSymbols = it
                onOptionsChange(state.options.copy(includeSymbols = it))
            }
            OptionRow(title = stringResource(R.string.option_avoid_ambiguous), checked = avoidAmbiguous) {
                avoidAmbiguous = it
                onOptionsChange(state.options.copy(avoidAmbiguous = it))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onRegenerate) {
                    Text(text = stringResource(R.string.action_generate))
                }
                Button(onClick = onCopy) {
                    Text(text = stringResource(R.string.action_copy))
                }
            }
        }
    }
}

@Composable
private fun OptionRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title)
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
    }
}
