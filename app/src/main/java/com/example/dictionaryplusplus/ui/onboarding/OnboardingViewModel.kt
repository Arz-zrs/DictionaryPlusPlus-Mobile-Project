package com.example.dictionaryplusplus.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryplusplus.data.local.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferences: UserPreferences
): ViewModel() {
    private val _uiState = MutableStateFlow<OnboardingUiState>(OnboardingUiState.Active)
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun completeOnboarding(selectedNotificationTime: String) {
        viewModelScope.launch {
            userPreferences.setNotificationTime(selectedNotificationTime)
            userPreferences.setHasSeenOnboarding(true)
            _uiState.value = OnboardingUiState.Completed
        }
    }
}