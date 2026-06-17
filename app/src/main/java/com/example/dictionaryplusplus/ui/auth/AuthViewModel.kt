package com.example.dictionaryplusplus.ui.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryplusplus.R
import com.example.dictionaryplusplus.domain.repository.SyncRepository
import com.example.dictionaryplusplus.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val syncRepository: SyncRepository
): ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _displayNameInput = MutableStateFlow("")
    val displayNameInput: StateFlow<String> = _displayNameInput.asStateFlow()

    private val _emailInput = MutableStateFlow("")
    val emailInput: StateFlow<String> = _emailInput.asStateFlow()

    private val _passwordInput = MutableStateFlow("")
    val passwordInput: StateFlow<String> = _passwordInput.asStateFlow()

    private val _confirmPasswordInput = MutableStateFlow("")
    val confirmPasswordInput: StateFlow<String> = _confirmPasswordInput.asStateFlow()

    private val _displayNameError = MutableStateFlow<UiText?>(null)
    val displayNameError: StateFlow<UiText?> = _displayNameError.asStateFlow()

    private val _emailError = MutableStateFlow<UiText?>(null)
    val emailError: StateFlow<UiText?> = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<UiText?>(null)
    val passwordError: StateFlow<UiText?> = _passwordError.asStateFlow()

    private val _confirmPasswordError = MutableStateFlow<UiText?>(null)
    val confirmPasswordError: StateFlow<UiText?> = _confirmPasswordError.asStateFlow()

    fun onDisplayNameChange(value: String) {
        _displayNameInput.value = value
    }

    fun onEmailChange(value: String) {
        _emailInput.value = value
    }

    fun onPasswordChange(value: String) {
        _passwordInput.value = value
    }

    fun onConfirmPasswordChange(value: String) {
        _confirmPasswordInput.value = value
    }

    fun login() {
        val email = _emailInput.value.trim()
        val password = _passwordInput.value

        val isEmailValid = validateEmail(email)
        val isPasswordValid = validatePassword(password)

        if (!isEmailValid || !isPasswordValid) return

        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            syncRepository.login(email, password)
                .onSuccess { userProfile ->
                    _uiState.value = AuthUiState.Success(userProfile)
                }
                .onFailure { exception ->
                    _uiState.value = AuthUiState.Error(
                        exception.message?.let { UiText.DynamicString(it) }
                            ?: UiText.StringResource(R.string.error_login_failed)
                    )
                }
        }
    }

    fun register() {
        val displayName = _displayNameInput.value.trim()
        val email = _emailInput.value.trim()
        val password = _passwordInput.value
        val confirmPassword = _confirmPasswordInput.value

        val isDisplayNameValid = validateDisplayName(displayName)
        val isEmailValid = validateEmail(email)
        val isPasswordValid = validatePassword(password)
        val isConfirmPasswordValid = validateConfirmPassword(password, confirmPassword)

        if (!isDisplayNameValid || !isEmailValid ||
            !isPasswordValid || !isConfirmPasswordValid) return

        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            syncRepository.register(displayName, email, password)
                .onSuccess { userProfile ->
                    _uiState.value = AuthUiState.Success(userProfile)
                }
                .onFailure { exception ->
                    _uiState.value = AuthUiState.Error(
                        exception.message?.let { UiText.DynamicString(it) }
                            ?: UiText.StringResource(R.string.error_registration_failed)
                    )
                }
        }
    }

    private fun validateDisplayName(name: String): Boolean {
        return if (name.isEmpty()) {
            _displayNameError.value = UiText.StringResource(R.string.error_username_empty)
            false
        } else {
            _displayNameError.value = null
            true
        }
    }

    private fun validateEmail(email: String): Boolean {
        return if (email.isEmpty()) {
            _emailError.value = UiText.StringResource(R.string.error_email_empty)
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailError.value = UiText.StringResource(R.string.error_email_invalid)
            false
        } else {
            _emailError.value = null
            true
        }
    }

    private fun validatePassword(password: String): Boolean {
        return if (password.length < 8) {
            _passwordError.value = UiText.StringResource(R.string.error_password_too_short)
            false
        } else {
            _passwordError.value = null
            true
        }
    }

    private fun validateConfirmPassword(password: String, confirm: String): Boolean {
        return if (password != confirm) {
            _confirmPasswordError.value = UiText.StringResource(R.string.error_passwords_not_match)
            false
        } else {
            _confirmPasswordError.value = null
            true
        }
    }
}