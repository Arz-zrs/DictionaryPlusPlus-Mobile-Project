package com.example.dictionaryplusplus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryplusplus.domain.model.FontSize
import com.example.dictionaryplusplus.domain.model.ThemeMode
import com.example.dictionaryplusplus.domain.repository.AuthRepository
import com.example.dictionaryplusplus.domain.repository.UserRepository
import com.example.dictionaryplusplus.domain.usecase.GetFontSizeUseCase
import com.example.dictionaryplusplus.domain.usecase.GetThemeModeUseCase
import com.example.dictionaryplusplus.domain.usecase.ObserveHasSeenOnboardingUseCase
import com.example.dictionaryplusplus.core.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val observeHasSeenOnboardingUseCase: ObserveHasSeenOnboardingUseCase,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    getThemeModeUseCase: GetThemeModeUseCase,
    getFontSizeUseCase: GetFontSizeUseCase
) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination.asStateFlow()

    val themeMode: StateFlow<ThemeMode> = getThemeModeUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeMode.SYSTEM
        )

    val fontSize: StateFlow<FontSize> = getFontSizeUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FontSize.MEDIUM
        )

    init {
        determineStartDestination()
    }

    private fun determineStartDestination() {
        viewModelScope.launch {
            val hasSeenOnboarding = observeHasSeenOnboardingUseCase().first()
            if (!hasSeenOnboarding) {
                _startDestination.value = Screen.Onboarding.route
                return@launch
            }

            val isUserLoggedIn = authRepository.isUserSessionActive()
            val hasUserProfile = userRepository.getUserProfile() != null
            _startDestination.value = when {
                isUserLoggedIn && hasUserProfile -> Screen.Dashboard.route
                else -> Screen.Login.route
            }
        }
    }
}
