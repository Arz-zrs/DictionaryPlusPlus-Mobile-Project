package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.domain.repository.OnboardingRepository
import javax.inject.Inject

class CompleteOnboardingUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) {
    suspend operator fun invoke(notificationTime: String) {
        onboardingRepository.completeOnboarding(notificationTime)
    }
}
