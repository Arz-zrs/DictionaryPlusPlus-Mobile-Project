package com.example.dictionaryplusplus.ui.auth

import com.example.dictionaryplusplus.domain.model.UserProfile
import com.example.dictionaryplusplus.util.ErrorMessage

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class Success(val userProfile: UserProfile) : AuthUiState
    data class Error(val errorMessage: ErrorMessage) : AuthUiState
}
