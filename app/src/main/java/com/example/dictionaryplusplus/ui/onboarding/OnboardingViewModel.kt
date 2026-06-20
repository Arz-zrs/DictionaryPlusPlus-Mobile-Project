package com.example.dictionaryplusplus.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryplusplus.data.local.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferences: UserPreferences
): ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun moveToNextStep() {
        _uiState.update { it.copy(currentStep = 2) }
    }

    fun onTimeSelected(hour: Int, minute: Int) {
        _uiState.update { it.copy(selectedHour = hour, selectedMinute = minute) }
    }

    fun completeOnboarding() {
        val state = _uiState.value
        val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", state.selectedHour, state.selectedMinute)
        saveOnboarding(formattedTime)
    }

    fun skipOnboarding() {
        saveOnboarding("08:00")
    }

    private fun saveOnboarding(selectedNotificationTime: String) {
        viewModelScope.launch {
            userPreferences.setNotificationTime(selectedNotificationTime)
            userPreferences.setHasSeenOnboarding(true)
            _uiState.update { it.copy(isCompleted = true) }
        }
    }
}
