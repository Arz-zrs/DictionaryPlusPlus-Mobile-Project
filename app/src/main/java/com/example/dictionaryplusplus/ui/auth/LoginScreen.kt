package com.example.dictionaryplusplus.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.dictionaryplusplus.R
import com.example.dictionaryplusplus.ui.components.AuthTextField
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dictionaryplusplus.core.util.ErrorMessage
import com.example.dictionaryplusplus.ui.theme.Success

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val resetEmailState by viewModel.resetEmailState.collectAsStateWithLifecycle()

    var showResetDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.login_welcome),
                style = MaterialTheme.typography.headlineMedium
            )

            AuthTextField(
                value = formState.email,
                onValueChange = { viewModel.onAction(AuthAction.OnEmailChange(it)) },
                label = stringResource(R.string.label_email),
                errorMessage = formState.emailError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            AuthTextField(
                value = formState.password,
                onValueChange = { viewModel.onAction(AuthAction.OnPasswordChange(it)) },
                label = stringResource(R.string.label_password),
                errorMessage = formState.passwordError,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            if (uiState is AuthUiState.Error) {
                val errorText = when (val error = (uiState as AuthUiState.Error).errorMessage) {
                    is ErrorMessage.Known -> stringResource(error.messageRes)
                    ErrorMessage.None -> ""
                }
                if (errorText.isNotEmpty()) {
                    Text(
                        text = errorText,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (uiState is AuthUiState.Loading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { viewModel.onAction(AuthAction.Login) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.btn_login))
                }

                TextButton(onClick = onNavigateToRegister) {
                    Text(text = stringResource(R.string.btn_no_account_register))
                }

                TextButton(
                    onClick = { showResetDialog = true }
                ) {
                    Text(stringResource(R.string.btn_forgot_password))
                }

                if (showResetDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            showResetDialog = false
                            resetEmail = ""
                            viewModel.resetPasswordResetState()
                        },
                        title = { Text(stringResource(R.string.reset_password_title)) },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(stringResource(R.string.reset_password_desc))
                                OutlinedTextField(
                                    value = resetEmail,
                                    onValueChange = { resetEmail = it },
                                    label = { Text(stringResource(R.string.label_email)) },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                when (resetEmailState) {
                                    is ResetEmailState.Sent -> Text(
                                        text = stringResource(R.string.reset_password_sent),
                                        color = Success,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    is ResetEmailState.Error -> Text(
                                        text = stringResource(R.string.reset_password_error),
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    else -> {}
                                }
                            }
                        },
                        confirmButton = {
                            if (resetEmailState is ResetEmailState.Loading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            } else {
                                TextButton(
                                    onClick = { viewModel.sendPasswordReset(resetEmail) },
                                    enabled = resetEmail.isNotBlank()
                                ) {
                                    Text(stringResource(R.string.reset_password_send))
                                }
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    showResetDialog = false
                                    resetEmail = ""
                                    viewModel.resetPasswordResetState()
                                }
                            ) {
                                Text(stringResource(R.string.btn_cancel))
                            }
                        }
                    )
                }
            }
        }
    }
}
