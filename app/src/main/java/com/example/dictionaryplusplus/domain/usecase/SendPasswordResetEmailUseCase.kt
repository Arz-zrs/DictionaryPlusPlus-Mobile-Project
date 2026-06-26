package com.example.dictionaryplusplus.domain.usecase

import android.util.Patterns
import com.example.dictionaryplusplus.domain.repository.AuthRepository
import javax.inject.Inject

class SendPasswordResetEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        if (email.isBlank()) return Result.failure(Exception("Email cannot be empty"))
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("Invalid email"))
        }
        return authRepository.sendPasswordResetEmail(email)
    }
}