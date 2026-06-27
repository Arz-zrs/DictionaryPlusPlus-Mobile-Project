package com.example.dictionaryplusplus.domain.usecase.auth

import com.example.dictionaryplusplus.domain.repository.AuthRepository
import javax.inject.Inject

class SendPasswordResetEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        return authRepository.sendPasswordResetEmail(email)
    }
}