package com.example.dictionaryplusplus.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryplusplus.R
import com.example.dictionaryplusplus.core.util.asErrorMessage
import com.example.dictionaryplusplus.domain.model.FontSize
import com.example.dictionaryplusplus.domain.model.ThemeMode
import com.example.dictionaryplusplus.domain.usecase.ChangePasswordUseCase
import com.example.dictionaryplusplus.domain.usecase.GetDailyQuizRefreshTimeUseCase
import com.example.dictionaryplusplus.domain.usecase.GetFontSizeUseCase
import com.example.dictionaryplusplus.domain.usecase.GetNotificationPermissionStatusUseCase
import com.example.dictionaryplusplus.domain.usecase.GetNotificationTimeUseCase
import com.example.dictionaryplusplus.domain.usecase.GetQuizLengthUseCase
import com.example.dictionaryplusplus.domain.usecase.GetThemeModeUseCase
import com.example.dictionaryplusplus.domain.usecase.LogoutUseCase
import com.example.dictionaryplusplus.domain.usecase.ObserveUserProfileUseCase
import com.example.dictionaryplusplus.domain.usecase.RescheduleWotdUseCase
import com.example.dictionaryplusplus.domain.usecase.ResetQuizCompletionUseCase
import com.example.dictionaryplusplus.domain.usecase.SetDailyQuizRefreshTimeUseCase
import com.example.dictionaryplusplus.domain.usecase.SetFontSizeUseCase
import com.example.dictionaryplusplus.domain.usecase.SetQuizLengthUseCase
import com.example.dictionaryplusplus.domain.usecase.SetThemeModeUseCase
import com.example.dictionaryplusplus.domain.usecase.TriggerWotdWorkerUseCase
import com.example.dictionaryplusplus.domain.usecase.UpdateDisplayNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getQuizLengthUseCase: GetQuizLengthUseCase,
    private val setQuizLengthUseCase: SetQuizLengthUseCase,
    getDailyQuizRefreshTimeUseCase: GetDailyQuizRefreshTimeUseCase,
    private val setDailyQuizRefreshTimeUseCase: SetDailyQuizRefreshTimeUseCase,
    getThemeModeUseCase: GetThemeModeUseCase,
    private val setThemeModeUseCase: SetThemeModeUseCase,
    getFontSizeUseCase: GetFontSizeUseCase,
    private val setFontSizeUseCase: SetFontSizeUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    getNotificationTimeUseCase: GetNotificationTimeUseCase,
    private val rescheduleWotdUseCase: RescheduleWotdUseCase,
    private val triggerWotdWorkerUseCase: TriggerWotdWorkerUseCase,
    private val resetQuizCompletionUseCase: ResetQuizCompletionUseCase,
    private val updateDisplayNameUseCase: UpdateDisplayNameUseCase,
    observeUserProfileUseCase: ObserveUserProfileUseCase,
    private val getNotificationPermissionStatusUseCase: GetNotificationPermissionStatusUseCase
): ViewModel() {
    private val _passwordUiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Idle)
    val passwordUiState: StateFlow<SettingsUiState> = _passwordUiState.asStateFlow()

    private val _displayNameUiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Idle)
    val displayNameUiState: StateFlow<SettingsUiState> = _displayNameUiState.asStateFlow()

    val quizLength: StateFlow<Int> = getQuizLengthUseCase()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialValue = 5)

    val dailyQuizRefreshTime: StateFlow<String> = getDailyQuizRefreshTimeUseCase()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialValue = "08:00"
        )

    val themeMode: StateFlow<ThemeMode> = getThemeModeUseCase()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeMode.SYSTEM
        )

    val fontSize: StateFlow<FontSize> = getFontSizeUseCase()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialValue = FontSize.MEDIUM
        )

    fun updateQuizLength(length: Int) {
        viewModelScope.launch {
            setQuizLengthUseCase(length)
        }
    }

    fun updateQuizRefreshTime(time: String) {
        viewModelScope.launch {
            setDailyQuizRefreshTimeUseCase(time)
        }
    }

    fun updateThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            setThemeModeUseCase(mode)
        }
    }

    fun updateFontSize(size: FontSize) {
        viewModelScope.launch {
            setFontSizeUseCase(size)
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase().onSuccess { onSuccess() }
        }
    }

    val displayName: StateFlow<String> = observeUserProfileUseCase()
        .map { it?.displayName ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    fun updateDisplayName(name: String) {
        viewModelScope.launch {
            _displayNameUiState.value = SettingsUiState.Loading
            updateDisplayNameUseCase(name)
                .onSuccess {
                    _displayNameUiState.value = SettingsUiState.Success
                }
                .onFailure {
                    _displayNameUiState.value = SettingsUiState.Error(
                        asErrorMessage(R.string.error_update_display_name_failed)
                    )
                }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _passwordUiState.value = SettingsUiState.Loading
            changePasswordUseCase(currentPassword, newPassword)
                .onSuccess {
                    _passwordUiState.value = SettingsUiState.Success
                }
                .onFailure {
                    _passwordUiState.value = SettingsUiState.Error(asErrorMessage(R.string.change_password_failed))
                }
        }
    }

    fun resetPasswordState() {
        _passwordUiState.value = SettingsUiState.Idle
    }

    fun resetDisplayNameState() {
        _displayNameUiState.value = SettingsUiState.Idle
    }

    val notificationTime: StateFlow<String> = getNotificationTimeUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), initialValue = "08:00")

    fun updateNotificationTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            rescheduleWotdUseCase(hour, minute)
        }
    }

    fun triggerWotd() {
        triggerWotdWorkerUseCase()
    }

    fun resetQuiz() {
        viewModelScope.launch {
            resetQuizCompletionUseCase()
        }
    }

    private val _isNotificationPermissionGranted = MutableStateFlow(
        getNotificationPermissionStatusUseCase()
    )
    val isNotificationPermissionGranted: StateFlow<Boolean> =
        _isNotificationPermissionGranted.asStateFlow()

    fun refreshPermissionState() {
        _isNotificationPermissionGranted.value = getNotificationPermissionStatusUseCase()
    }
}
