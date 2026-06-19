package com.example.dictionaryplusplus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryplusplus.data.local.UserPreferences
import com.example.dictionaryplusplus.domain.repository.AuthRepository
import com.example.dictionaryplusplus.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination.asStateFlow()

    init {
        determineStartDestination()
    }

    private fun determineStartDestination() {
        viewModelScope.launch {
            val hasSeenOnboarding = userPreferences.hasSeenOnboarding.first()
            if (!hasSeenOnboarding) {
                _startDestination.value = Screen.Onboarding.route
                return@launch
            }

            val isUserLoggedIn = authRepository.isUserSessionActive()
            if (isUserLoggedIn) {
                _startDestination.value = Screen.Dashboard.route
            } else {
                _startDestination.value = Screen.Login.route
            }
        }
    }
}
