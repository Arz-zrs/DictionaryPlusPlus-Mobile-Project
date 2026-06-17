package com.example.dictionaryplusplus.ui.auth

sealed interface AuthAction {
    data class OnDisplayNameChange(val value: String) : AuthAction
    data class OnEmailChange(val value: String) : AuthAction
    data class OnPasswordChange(val value: String) : AuthAction
    data class OnConfirmPasswordChange(val value: String) : AuthAction
    object Login : AuthAction
    object Register : AuthAction
}
