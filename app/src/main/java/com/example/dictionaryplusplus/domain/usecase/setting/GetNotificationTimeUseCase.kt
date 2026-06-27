package com.example.dictionaryplusplus.domain.usecase.setting

import com.example.dictionaryplusplus.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotificationTimeUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) {
    operator fun invoke(): Flow<String> = onboardingRepository.notificationTime
}