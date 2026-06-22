package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.domain.repository.AuthRepository
import com.example.dictionaryplusplus.domain.repository.UserRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            authRepository.logout()
            userRepository.clearLocalProfile()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}