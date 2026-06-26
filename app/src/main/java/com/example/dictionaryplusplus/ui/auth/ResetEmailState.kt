package com.example.dictionaryplusplus.ui.auth

sealed interface ResetEmailState {
    object Idle : ResetEmailState
    object Loading : ResetEmailState
    object Sent : ResetEmailState
    object Error : ResetEmailState
}