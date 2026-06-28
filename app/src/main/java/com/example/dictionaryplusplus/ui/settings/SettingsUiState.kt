package com.example.dictionaryplusplus.ui.settings

import com.example.dictionaryplusplus.util.ErrorMessage

sealed interface SettingsUiState {
    object Idle: SettingsUiState
    object Loading: SettingsUiState
    object Success: SettingsUiState
    data class Error(val errorMessage: ErrorMessage): SettingsUiState
}
