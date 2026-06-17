package com.example.dictionaryplusplus.ui.auth

import com.example.dictionaryplusplus.util.UiText

data class AuthFormState(
    val displayName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val displayNameError: UiText? = null,
    val emailError: UiText? = null,
    val passwordError: UiText? = null,
    val confirmPasswordError: UiText? = null
)