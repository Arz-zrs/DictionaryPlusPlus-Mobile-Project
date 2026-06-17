package com.example.dictionaryplusplus.ui.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryplusplus.R
import com.example.dictionaryplusplus.domain.repository.AuthRepository
import com.example.dictionaryplusplus.util.UiText
import com.example.dictionaryplusplus.util.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(AuthFormState())
    val formState: StateFlow<AuthFormState> = _formState.asStateFlow()

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.OnDisplayNameChange -> {
                _formState.update { it.copy(displayName = action.value, displayNameError = null) }
            }
            is AuthAction.OnEmailChange -> {
                _formState.update { it.copy(email = action.value, emailError = null) }
            }
            is AuthAction.OnPasswordChange -> {
                _formState.update { it.copy(password = action.value, passwordError = null) }
            }
            is AuthAction.OnConfirmPasswordChange -> {
                _formState.update { it.copy(confirmPassword = action.value, confirmPasswordError = null) }
            }
            AuthAction.Login -> login()
            AuthAction.Register -> register()
        }
    }

    private fun login() {
        val state = _formState.value
        val emailError = validateEmail(state.email)
        val passwordError = validatePassword(state.password)

        val hasError = listOf(emailError, passwordError).any { it != null }

        if (hasError) {
            _formState.update { it.copy(emailError = emailError, passwordError = passwordError) }
            return
        }

        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            authRepository.login(state.email.trim(), state.password)
                .onSuccess { userProfile ->
                    _uiState.value = AuthUiState.Success(userProfile)
                }
                .onFailure { exception ->
                    _uiState.value = AuthUiState.Error(exception.asUiText(R.string.error_login_failed))
                }
        }
    }

    private fun register() {
        val state = _formState.value
        val displayNameError = validateDisplayName(state.displayName)
        val emailError = validateEmail(state.email)
        val passwordError = validatePassword(state.password)
        val confirmPasswordError = validateConfirmPassword(state.password, state.confirmPassword)

        val hasError = listOf(
            displayNameError,
            emailError,
            passwordError,
            confirmPasswordError
        ).any { it != null }

        if (hasError) {
            _formState.update { 
                it.copy(
                    displayNameError = displayNameError,
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError
                ) 
            }
            return
        }

        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            authRepository.register(state.displayName.trim(), state.email.trim(), state.password)
                .onSuccess { userProfile ->
                    _uiState.value = AuthUiState.Success(userProfile)
                }
                .onFailure { exception ->
                    _uiState.value = AuthUiState.Error(exception.asUiText(R.string.error_registration_failed))
                }
        }
    }

    private fun validateDisplayName(name: String): UiText? {
        return if (name.isEmpty()) {
            UiText.StringResource(R.string.error_username_empty)
        } else {
            null
        }
    }

    private fun validateEmail(email: String): UiText? {
        return if (email.isEmpty()) {
            UiText.StringResource(R.string.error_email_empty)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            UiText.StringResource(R.string.error_email_invalid)
        } else {
            null
        }
    }

    private fun validatePassword(password: String): UiText? {
        return if (password.length < 8) {
            UiText.StringResource(R.string.error_password_too_short)
        } else {
            null
        }
    }

    private fun validateConfirmPassword(password: String, confirm: String): UiText? {
        return if (password != confirm) {
            UiText.StringResource(R.string.error_passwords_not_match)
        } else {
            null
        }
    }
}