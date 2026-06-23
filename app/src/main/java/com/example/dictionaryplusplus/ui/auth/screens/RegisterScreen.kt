package com.example.dictionaryplusplus.ui.auth.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.dictionaryplusplus.ui.auth.AuthAction
import com.example.dictionaryplusplus.ui.auth.AuthUiState
import com.example.dictionaryplusplus.ui.auth.AuthViewModel
import com.example.dictionaryplusplus.util.ErrorMessage

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onRegisterSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.register_title),
                style = MaterialTheme.typography.headlineMedium
            )

            AuthTextField(
                value = formState.displayName,
                onValueChange = { viewModel.onAction(AuthAction.OnDisplayNameChange(it)) },
                label = stringResource(R.string.label_username),
                errorMessage = formState.displayNameError
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
                label = stringResource(R.string.label_password_hint),
                errorMessage = formState.passwordError,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            AuthTextField(
                value = formState.confirmPassword,
                onValueChange = { viewModel.onAction(AuthAction.OnConfirmPasswordChange(it)) },
                label = stringResource(R.string.label_confirm_password),
                errorMessage = formState.confirmPasswordError,
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
                    onClick = { viewModel.onAction(AuthAction.Register) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.btn_register))
                }

                TextButton(onClick = onNavigateBack) {
                    Text(text = stringResource(R.string.btn_back_to_login))
                }
            }
        }
    }
}
