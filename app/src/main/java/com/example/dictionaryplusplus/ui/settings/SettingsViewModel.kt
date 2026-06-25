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
import com.example.dictionaryplusplus.domain.usecase.GetQuizLengthUseCase
import com.example.dictionaryplusplus.domain.usecase.GetThemeModeUseCase
import com.example.dictionaryplusplus.domain.usecase.LogoutUseCase
import com.example.dictionaryplusplus.domain.usecase.SetDailyQuizRefreshTimeUseCase
import com.example.dictionaryplusplus.domain.usecase.SetFontSizeUseCase
import com.example.dictionaryplusplus.domain.usecase.SetQuizLengthUseCase
import com.example.dictionaryplusplus.domain.usecase.SetThemeModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val changePasswordUseCase: ChangePasswordUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Idle)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

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

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            changePasswordUseCase(currentPassword, newPassword)
                .onSuccess {
                    _uiState.value = SettingsUiState.Success
                }
                .onFailure {
                    _uiState.value = SettingsUiState.Error(asErrorMessage(R.string.change_password_failed))
                }
        }
    }

    fun resetPasswordState() {
        _uiState.value = SettingsUiState.Idle
    }
}