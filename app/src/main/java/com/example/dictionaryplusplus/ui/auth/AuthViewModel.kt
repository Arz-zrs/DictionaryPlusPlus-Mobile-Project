package com.example.dictionaryplusplus.ui.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryplusplus.R
import com.example.dictionaryplusplus.domain.usecase.LoginUseCase
import com.example.dictionaryplusplus.domain.usecase.RegisterUseCase
import com.example.dictionaryplusplus.core.util.ErrorMessage
import com.example.dictionaryplusplus.core.util.asErrorMessage
import com.example.dictionaryplusplus.domain.usecase.SendPasswordResetEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(AuthFormState())
    val formState: StateFlow<AuthFormState> = _formState.asStateFlow()

    private val _resetEmailState = MutableStateFlow<ResetEmailState>(ResetEmailState.Idle)
    val resetEmailState: StateFlow<ResetEmailState> = _resetEmailState.asStateFlow()


    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.OnDisplayNameChange -> {
                _formState.update { it.copy(displayName = action.value, displayNameError = ErrorMessage.None) }
            }
            is AuthAction.OnEmailChange -> {
                _formState.update { it.copy(email = action.value, emailError = ErrorMessage.None) }
            }
            is AuthAction.OnPasswordChange -> {
                _formState.update { it.copy(password = action.value, passwordError = ErrorMessage.None) }
            }
            is AuthAction.OnConfirmPasswordChange -> {
                _formState.update { it.copy(confirmPassword = action.value, confirmPasswordError = ErrorMessage.None) }
            }
            AuthAction.Login -> login()
            AuthAction.Register -> register()
        }
    }

    private fun login() {
        val state = _formState.value
        val emailError = validateEmail(state.email)
        val passwordError = validatePassword(state.password)

        val hasError = listOf(emailError, passwordError).any { it !is ErrorMessage.None }

        if (hasError) {
            _formState.update { it.copy(emailError = emailError, passwordError = passwordError) }
            return
        }

        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            loginUseCase(state.email.trim(), state.password)
                .onSuccess { _uiState.value = AuthUiState.Success(it) }
                .onFailure { _uiState.value = AuthUiState.Error(asErrorMessage(R.string.error_login_failed)) }
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
        ).any { it !is ErrorMessage.None }

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
            registerUseCase(state.displayName.trim(), state.email.trim(), state.password)
                .onSuccess { _uiState.value = AuthUiState.Success(it) }
                .onFailure { _uiState.value = AuthUiState.Error(asErrorMessage(R.string.error_registration_failed)) }
        }
    }

    private fun validateDisplayName(name: String): ErrorMessage {
        return if (name.isEmpty()) {
            ErrorMessage.Known(R.string.error_display_name_empty)
        } else {
            ErrorMessage.None
        }
    }

    private fun validateEmail(email: String): ErrorMessage {
        return if (email.isEmpty()) {
            ErrorMessage.Known(R.string.error_email_empty)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ErrorMessage.Known(R.string.error_email_invalid)
        } else {
            ErrorMessage.None
        }
    }

    private fun validatePassword(password: String): ErrorMessage {
        return if (password.length < 8) {
            ErrorMessage.Known(R.string.error_password_too_short)
        } else {
            ErrorMessage.None
        }
    }

    private fun validateConfirmPassword(password: String, confirm: String): ErrorMessage {
        return if (password != confirm) {
            ErrorMessage.Known(R.string.error_passwords_not_match)
        } else {
            ErrorMessage.None
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            _resetEmailState.value = ResetEmailState.Loading
            sendPasswordResetEmailUseCase(email)
                .onSuccess { _resetEmailState.value = ResetEmailState.Sent }
                .onFailure { _resetEmailState.value = ResetEmailState.Error }
        }
    }

    fun resetPasswordResetState() {
        _resetEmailState.value = ResetEmailState.Idle
    }
}
