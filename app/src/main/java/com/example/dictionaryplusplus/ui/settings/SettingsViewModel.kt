package com.example.dictionaryplusplus.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryplusplus.R
import com.example.dictionaryplusplus.core.util.asErrorMessage
import com.example.dictionaryplusplus.domain.usecase.ChangePasswordUseCase
import com.example.dictionaryplusplus.domain.usecase.GetDailyQuizRefreshTimeUseCase
import com.example.dictionaryplusplus.domain.usecase.GetQuizLengthUseCase
import com.example.dictionaryplusplus.domain.usecase.LogoutUseCase
import com.example.dictionaryplusplus.domain.usecase.SetDailyQuizRefreshTimeUseCase
import com.example.dictionaryplusplus.domain.usecase.SetQuizLengthUseCase
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