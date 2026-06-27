package com.example.dictionaryplusplus.domain.usecase.auth

import com.example.dictionaryplusplus.domain.model.UserProfile
import com.example.dictionaryplusplus.domain.repository.AuthRepository
import com.example.dictionaryplusplus.domain.repository.UserProfileRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserProfileRepository
) {
    suspend operator fun invoke(
        displayName: String,
        email: String,
        password: String
    ): Result<UserProfile> {
        return try {
            val uid = authRepository.register(email, password).getOrThrow()
            userRepository.createProfile(uid, displayName, email).getOrElse {
                authRepository.logout()
                return Result.failure(it)
            }
            userRepository.getUserProfile()?.let { Result.success(it) }
                ?: Result.failure(Exception("User profile not found after registration"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}