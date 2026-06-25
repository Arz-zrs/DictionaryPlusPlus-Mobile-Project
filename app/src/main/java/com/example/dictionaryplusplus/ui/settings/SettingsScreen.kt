package com.example.dictionaryplusplus.ui.settings

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dictionaryplusplus.R
import com.example.dictionaryplusplus.core.util.ErrorMessage
import com.example.dictionaryplusplus.ui.theme.Success
import java.util.Locale

@Composable
fun SettingsScreen(
    onLogoutSuccess: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val quizLength by viewModel.quizLength.collectAsStateWithLifecycle()
    val refreshTime by viewModel.dailyQuizRefreshTime.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is SettingsUiState.Success) {
            currentPassword = ""
            newPassword = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 8.dp)
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stringResource(R.string.settings_quiz_length), style = MaterialTheme.typography.titleMedium)
                Text(
                    text = stringResource(R.string.settings_quiz_length_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(3, 5, 7).forEach { length ->
                        FilterChip(
                            selected = quizLength == length,
                            onClick = { viewModel.updateQuizLength(length) },
                            label = { Text(stringResource(R.string.settings_quiz_length_format, length)) }
                        )
                    }
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stringResource(R.string.settings_reset_time), style = MaterialTheme.typography.titleMedium)
                Text(
                    text = stringResource(R.string.settings_reset_time_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        val parts = refreshTime.split(":")
                        val hour = parts.getOrNull(0)?.toInt() ?: 6
                        val minute = parts.getOrNull(1)?.toInt() ?: 0
                        TimePickerDialog(context, { _, h, m ->
                            viewModel.updateQuizRefreshTime(String.format(Locale.getDefault().toString(), h, m))
                        }, hour, minute, true).show()
                    }
                ) {
                    Text(stringResource(R.string.settings_selected_time, refreshTime))
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.settings_change_password),
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = {
                        currentPassword = it
                        if (uiState is SettingsUiState.Idle) viewModel.resetPasswordState()
                    },
                    label = { Text(stringResource(R.string.settings_current_password)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = {
                        currentPassword = it
                        if (uiState is SettingsUiState.Idle) viewModel.resetPasswordState()
                    },
                    label = { Text(stringResource(R.string.settings_new_password_hint)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                when (uiState) {
                    is SettingsUiState.Loading -> {
                        CircularProgressIndicator()
                    }
                    is SettingsUiState.Success -> {
                        Text(
                            text = stringResource(R.string.settings_password_changed_success),
                            color = Success
                        )
                    }
                    is SettingsUiState.Error -> {
                        val message = when (val state = (uiState as SettingsUiState.Error).errorMessage) {
                            is ErrorMessage.Known -> stringResource(state.messageRes)
                            ErrorMessage.None -> ""
                        }

                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    else -> {}
                }

                Button(
                    onClick = {
                        if (currentPassword.isNotEmpty() && newPassword.length >= 8) {
                            viewModel.changePassword(currentPassword, newPassword)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.settings_update_password))
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.logout(onLogoutSuccess) },
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.settings_logout),
                color = MaterialTheme.colorScheme.onError
            )
        }
    }
}
