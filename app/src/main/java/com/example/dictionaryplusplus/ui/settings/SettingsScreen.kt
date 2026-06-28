package com.example.dictionaryplusplus.ui.settings

import android.Manifest
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import com.example.dictionaryplusplus.R
import com.example.dictionaryplusplus.core.util.ErrorMessage
import com.example.dictionaryplusplus.domain.model.FontSize
import com.example.dictionaryplusplus.domain.model.ThemeMode
import com.example.dictionaryplusplus.ui.theme.Success
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SettingsScreen(
    onLogoutSuccess: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = LocalContext.current as Activity
    val scrollState = rememberScrollState()

    val quizLength by viewModel.quizLength.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val fontSize by viewModel.fontSize.collectAsStateWithLifecycle()
    val passwordUiState by viewModel.passwordUiState.collectAsStateWithLifecycle()
    val displayNameUiState by viewModel.displayNameUiState.collectAsStateWithLifecycle()
    val notificationTime by viewModel.notificationTime.collectAsStateWithLifecycle()
    val displayName by viewModel.displayName.collectAsStateWithLifecycle()
    val isNotificationGranted by viewModel.isNotificationPermissionGranted.collectAsStateWithLifecycle()

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var nameInput by remember(displayName) { mutableStateOf(displayName) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshPermissionState()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(passwordUiState) {
        if (passwordUiState is SettingsUiState.Success) {
            currentPassword = ""
            newPassword = ""
            delay(3000.milliseconds)
            viewModel.resetPasswordState()
        } else if (passwordUiState is SettingsUiState.Error) {
            delay(3000.milliseconds)
            viewModel.resetPasswordState()
        }
    }

    LaunchedEffect(displayNameUiState) {
        if (displayNameUiState is SettingsUiState.Success || displayNameUiState is SettingsUiState.Error) {
            delay(3000.milliseconds)
            viewModel.resetDisplayNameState()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.refreshPermissionState()
        if (isGranted) {
            val parts = notificationTime.split(":")
            val hour = parts.getOrNull(0)?.toIntOrNull() ?: 8
            val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
            viewModel.updateNotificationTime(hour, minute)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (!isNotificationGranted) {
            PermissionBanner(
                onEnableClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                            activity,
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                        if (shouldShowRationale) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", activity.packageName, null)
                            }
                            activity.startActivity(intent)
                        }
                    }
                }
            )
        }

        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 8.dp)
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(stringResource(R.string.settings_appearance), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))

                Text(stringResource(R.string.settings_theme_mode), style = MaterialTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ThemeMode.entries.forEach { mode ->
                        FilterChip(
                            selected = themeMode == mode,
                            onClick = { viewModel.updateThemeMode(mode) },
                            modifier = Modifier.padding(horizontal = 4.dp),
                            label = {
                                Text(
                                    when (mode) {
                                        ThemeMode.SYSTEM -> stringResource(R.string.settings_theme_system)
                                        ThemeMode.LIGHT -> stringResource(R.string.settings_theme_light)
                                        ThemeMode.DARK -> stringResource(R.string.settings_theme_dark)
                                    }
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(stringResource(R.string.settings_font_size), style = MaterialTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    FontSize.entries.forEach { size ->
                        FilterChip(
                            selected = fontSize == size,
                            onClick = { viewModel.updateFontSize(size) },
                            modifier = Modifier.padding(horizontal = 4.dp),
                            label = {
                                Text(
                                    when (size) {
                                        FontSize.SMALL -> stringResource(R.string.settings_font_small)
                                        FontSize.MEDIUM -> stringResource(R.string.settings_font_medium)
                                        FontSize.LARGE -> stringResource(R.string.settings_font_large)
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(stringResource(R.string.settings_quiz_length), style = MaterialTheme.typography.titleMedium)
                Text(
                    text = stringResource(R.string.settings_quiz_length_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    listOf(3, 5, 7).forEach { length ->
                        FilterChip(
                            selected = quizLength == length,
                            onClick = { viewModel.updateQuizLength(length) },
                            modifier = Modifier.padding(horizontal = 4.dp),
                            label = { Text(stringResource(R.string.settings_quiz_length_format, length)) }
                        )
                    }
                }
            }
        }


        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
            ) {
                Text(stringResource(R.string.settings_notification_time), style = MaterialTheme.typography.titleMedium)
                Text(
                    text = stringResource(R.string.settings_notification_time_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        val parts = notificationTime.split(":")
                        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 8
                        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
                        TimePickerDialog(context, { _, h, m ->
                            viewModel.updateNotificationTime(h, m)
                        }, hour, minute, true).show()
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(stringResource(R.string.settings_notification_time_format, notificationTime))
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.settings_display_name),
                    style = MaterialTheme.typography.titleMedium
                )
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = {
                        nameInput = it
                        if (displayNameUiState !is SettingsUiState.Idle) viewModel.resetDisplayNameState()
                    },
                    label = { Text(stringResource(R.string.label_display_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                when (displayNameUiState) {
                    is SettingsUiState.Loading -> {
                        CircularProgressIndicator()
                    }
                    is SettingsUiState.Success -> {
                        Text(
                            text = stringResource(R.string.settings_display_name_updated_success),
                            color = Success
                        )
                    }
                    is SettingsUiState.Error -> {
                        val message = when (val state = (displayNameUiState as SettingsUiState.Error).errorMessage) {
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
                    onClick = { viewModel.updateDisplayName(nameInput) },
                    enabled = nameInput.isNotBlank() && nameInput != displayName,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.settings_save_display_name))
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
                        if (passwordUiState !is SettingsUiState.Idle) viewModel.resetPasswordState()
                    },
                    label = { Text(stringResource(R.string.settings_current_password)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        if (passwordUiState !is SettingsUiState.Idle) viewModel.resetPasswordState()
                    },
                    label = { Text(stringResource(R.string.settings_new_password_hint)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                when (passwordUiState) {
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
                        val message = when (val state = (passwordUiState as SettingsUiState.Error).errorMessage) {
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

        DebugSection(
            onTriggerWotd = { viewModel.triggerWotd() }
        )

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

@Composable
fun PermissionBanner(
    onEnableClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.permission_banner_title),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    text = stringResource(R.string.permission_banner_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onEnableClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(
                    text = stringResource(R.string.permission_banner_btn),
                    color = MaterialTheme.colorScheme.onError
                )
            }
        }
    }
}