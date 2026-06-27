package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.domain.repository.UserProfileRepository
import javax.inject.Inject

class UpdateDisplayNameUseCase @Inject constructor(
    private val userRepository: UserProfileRepository
) {
    suspend operator fun invoke(displayName: String): Result<Unit> {
        return userRepository.updateDisplayName(displayName)
    }
}