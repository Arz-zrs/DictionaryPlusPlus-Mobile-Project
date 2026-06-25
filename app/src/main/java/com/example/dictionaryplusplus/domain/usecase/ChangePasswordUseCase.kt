package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.domain.repository.AuthRepository
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
){
    suspend operator fun invoke(currentPassword: String, newPassword: String): Result<Unit> {
        return authRepository.changePassword(currentPassword, newPassword)
    }
}