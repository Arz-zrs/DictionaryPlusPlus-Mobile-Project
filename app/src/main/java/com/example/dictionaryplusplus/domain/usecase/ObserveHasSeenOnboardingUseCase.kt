package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveHasSeenOnboardingUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return onboardingRepository.hasSeenOnboarding
    }
}
