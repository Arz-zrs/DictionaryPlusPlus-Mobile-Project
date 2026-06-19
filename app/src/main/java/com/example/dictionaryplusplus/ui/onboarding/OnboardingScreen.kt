package com.example.dictionaryplusplus.ui.onboarding

import android.app.TimePickerDialog
import android.os.Build
import java.util.Locale
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.dictionaryplusplus.R
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var currentStep by remember { mutableStateOf(1) }
    var selectedHour by remember { mutableStateOf(8) }
    var selectedMinute by remember { mutableStateOf(0) }

    LaunchedEffect(uiState) {
        if (uiState == OnboardingUiState.Completed) {
            onNavigateToLogin()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { _ ->
            currentStep = 2
        }
    )

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProgressDots(currentStep = currentStep)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (currentStep) {
                    1 -> {
                        StepOnePermissionExplanation(
                            onRequestPermission = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    currentStep = 2
                                }
                            }
                        )
                    }
                    else -> {
                        StepTwoTimeSelection(
                            selectedHour = selectedHour,
                            selectedMinute = selectedMinute,
                            onTimeSelectClick = {
                                TimePickerDialog(
                                    context,
                                    { _, hour: Int, minute: Int ->
                                        selectedHour = hour
                                        selectedMinute = minute
                                    },
                                    selectedHour,
                                    selectedMinute,
                                    true
                                ).show()
                            }
                        )
                    }
                }
            }
            BottomNavigationButtons(
                currentStep = currentStep,
                onSkip = {
                    viewModel.completeOnboarding("08:00")
                },
                onComplete = {
                    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
                    viewModel.completeOnboarding(formattedTime)
                }
            )
        }
    }
}

@Composable
private fun ProgressDots(currentStep: Int, totalSteps: Int = 2) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 16.dp)
    ) {
        repeat(totalSteps) { step ->
            val color = if (step + 1 == currentStep) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
private fun StepOnePermissionExplanation(
    onRequestPermission: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.onboarding_notification_title),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = stringResource(R.string.onboarding_notification_desc),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRequestPermission) {
            Text(stringResource(R.string.onboarding_btn_enable_notification))
        }
    }
}

@Composable
private fun StepTwoTimeSelection(
    selectedHour: Int,
    selectedMinute: Int,
    onTimeSelectClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.onboarding_time_title),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = stringResource(R.string.onboarding_time_desc),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            onClick = onTimeSelectClick,
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.width(180.dp)
        ) {
            Box(
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.time_format, selectedHour, selectedMinute),
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationButtons(
    currentStep: Int,
    onSkip: () -> Unit,
    onComplete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (currentStep) {
            2 -> {
                TextButton(onClick = onSkip) {
                    Text(
                        text = stringResource(R.string.onboarding_btn_skip),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(onClick = onComplete) {
                    Text(stringResource(R.string.onboarding_btn_finish))
                }
            }
            else -> {
                Spacer(modifier = Modifier.width(48.dp))
            }
        }
    }
}