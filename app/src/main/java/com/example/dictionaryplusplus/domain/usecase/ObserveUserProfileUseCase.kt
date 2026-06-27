package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.domain.model.UserProfile
import com.example.dictionaryplusplus.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUserProfileUseCase @Inject constructor(
    private val userRepository: UserProfileRepository
) {
    operator fun invoke(): Flow<UserProfile?> {
        return userRepository.observeUserProfile()
    }
}
