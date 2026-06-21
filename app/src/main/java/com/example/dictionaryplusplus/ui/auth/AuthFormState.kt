package com.example.dictionaryplusplus.ui.auth

import com.example.dictionaryplusplus.util.ErrorMessage

data class AuthFormState(
    val displayName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val displayNameError: ErrorMessage = ErrorMessage.None,
    val emailError: ErrorMessage = ErrorMessage.None,
    val passwordError: ErrorMessage = ErrorMessage.None,
    val confirmPasswordError: ErrorMessage = ErrorMessage.None
)
