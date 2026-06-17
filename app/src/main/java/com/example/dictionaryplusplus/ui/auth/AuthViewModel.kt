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

    val usernameInput = MutableStateFlow("")
    val emailInput = MutableStateFlow("")
    val passwordInput = MutableStateFlow("")
    val confirmPasswordInput = MutableStateFlow("")

    private val _usernameError = MutableStateFlow<UiText?>(null)
    val usernameError: StateFlow<UiText?> = _usernameError.asStateFlow()

    private val _emailError = MutableStateFlow<UiText?>(null)
    val emailError: StateFlow<UiText?> = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<UiText?>(null)
    val passwordError: StateFlow<UiText?> = _passwordError.asStateFlow()

    private val _confirmPasswordError = MutableStateFlow<UiText?>(null)
    val confirmPasswordError: StateFlow<UiText?> = _confirmPasswordError.asStateFlow()

    fun login() {
        val email = emailInput.value.trim()
        val password = passwordInput.value
        if (!validateEmail(email) || !validatePassword(password)) return

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
        val username = usernameInput.value.trim()
        val email = emailInput.value.trim()
        val password = passwordInput.value
        val confirmPassword = confirmPasswordInput.value

        val isUsernameValid = validateUsername(username)
        val isEmailValid = validateEmail(email)
        val isPasswordValid = validatePassword(password)
        val isConfirmPasswordValid = validateConfirmPassword(password, confirmPassword)

        if (!isUsernameValid || !isEmailValid ||
            !isPasswordValid || !isConfirmPasswordValid) return

        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            syncRepository.register(username, email, password)
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

    private fun validateUsername(name: String): Boolean {
        return if (name.isEmpty()) {
            _usernameError.value = UiText.StringResource(R.string.error_username_empty)
            false
        } else {
            _usernameError.value = null
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