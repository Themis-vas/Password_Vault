package com.passguard.app.ui.lock

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
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.passguard.app.R
import com.passguard.app.domain.model.LockState

@Composable
fun LockScreen(
    lockState: LockState,
    canUseBiometric: Boolean,
    onUnlock: (String, (Boolean) -> Unit) -> Unit,
    onCreatePin: (String, (Boolean) -> Unit) -> Unit,
    onAuthenticateBiometric: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(
                    id = if (lockState == LockState.NoPinSet) R.string.lock_set_pin_title else R.string.lock_unlock_title
                ),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = pin,
                onValueChange = {
                    if (it.length <= 8) pin = it.filter { char -> char.isDigit() }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.lock_pin_label)) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )
            if (lockState == LockState.NoPinSet) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = confirmPin,
                    onValueChange = {
                        if (it.length <= 8) confirmPin = it.filter { char -> char.isDigit() }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.lock_confirm_pin_label)) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )
            }
            errorMessage?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = stringResource(R.string.lock_error_prefix, it), color = MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (lockState == LockState.NoPinSet) {
                        if (pin.length < 4) {
                            errorMessage = stringResource(R.string.lock_error_short_pin)
                        } else if (pin != confirmPin) {
                            errorMessage = stringResource(R.string.lock_error_mismatch)
                        } else {
                            onCreatePin(pin) { success ->
                                if (success) {
                                    errorMessage = null
                                    pin = ""
                                    confirmPin = ""
                                } else {
                                    errorMessage = stringResource(R.string.lock_error_generic)
                                }
                            }
                        }
                    } else {
                        onUnlock(pin) { success ->
                            if (!success) {
                                errorMessage = stringResource(R.string.lock_error_invalid)
                            } else {
                                errorMessage = null
                                pin = ""
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = pin.length in 4..8
            ) {
                Text(text = if (lockState == LockState.NoPinSet) stringResource(R.string.lock_create_action) else stringResource(R.string.lock_unlock_action))
            }
            if (canUseBiometric && lockState != LockState.NoPinSet) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onAuthenticateBiometric) {
                    androidx.compose.material3.Icon(imageVector = Icons.Filled.Fingerprint, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.lock_use_biometric))
                }
            }
        }
    }
}
