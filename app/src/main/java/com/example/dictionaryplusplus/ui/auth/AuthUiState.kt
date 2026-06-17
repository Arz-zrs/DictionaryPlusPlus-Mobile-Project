package com.example.dictionaryplusplus.ui.auth

import com.example.dictionaryplusplus.domain.model.UserProfile
import com.example.dictionaryplusplus.util.UiText

sealed interface AuthUiState {
    object Idle: AuthUiState
    object Loading: AuthUiState
    data class Success(val userProfile: UserProfile): AuthUiState
    data class Error(val message: UiText): AuthUiState
}