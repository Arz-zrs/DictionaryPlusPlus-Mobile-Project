package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.domain.repository.UserRepository
import javax.inject.Inject

class UpdateDisplayNameUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(displayName: String): Result<Unit> {
        return userRepository.updateDisplayName(displayName)
    }
}