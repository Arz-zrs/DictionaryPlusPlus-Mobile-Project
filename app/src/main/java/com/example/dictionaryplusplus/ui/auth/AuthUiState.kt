package com.example.dictionaryplusplus.ui.auth

import com.example.dictionaryplusplus.domain.mapper.UserProfileMapper
import com.example.dictionaryplusplus.util.UiText

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val userProfile: UserProfileMapper) : AuthUiState
    data class Error(val message: UiText) : AuthUiState
}