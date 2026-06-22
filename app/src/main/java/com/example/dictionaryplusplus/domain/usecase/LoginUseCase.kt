package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.domain.model.UserProfile
import com.example.dictionaryplusplus.domain.repository.AuthRepository
import com.example.dictionaryplusplus.domain.repository.UserRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<UserProfile> {
        return try {
            val uid = authRepository.login(email, password).getOrThrow()
            userRepository.fetchAndSyncProfile(uid, email).onFailure {
                authRepository.logout()
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}