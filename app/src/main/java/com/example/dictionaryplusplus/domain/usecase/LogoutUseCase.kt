package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.domain.repository.AuthRepository
import com.example.dictionaryplusplus.domain.repository.UserProfileRepository
import com.example.dictionaryplusplus.domain.repository.UserSyncRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userProfileRepository: UserProfileRepository,
    private val userSyncRepository: UserSyncRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            authRepository.logout()
            userProfileRepository.clearLocalProfile()
            userSyncRepository.clearAllUserData()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}