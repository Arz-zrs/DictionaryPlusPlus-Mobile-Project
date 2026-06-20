package com.example.dictionaryplusplus.ui.onboarding


data class OnboardingUiState(
    val currentStep: OnboardingStep = OnboardingStep.WELCOME,
    val selectedHour: Int = 8,
    val selectedMinute: Int = 0,
    val isCompleted: Boolean = false
)
