package com.example.dictionaryplusplus.domain.usecase.setting

import com.example.dictionaryplusplus.domain.repository.NotificationScheduler
import com.example.dictionaryplusplus.domain.repository.OnboardingRepository
import javax.inject.Inject

class CompleteOnboardingUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
    private val notificationScheduler: NotificationScheduler
) {
    suspend operator fun invoke(notificationTime: String) {
        onboardingRepository.completeOnboarding(notificationTime)

        val parts = notificationTime.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 6
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
        notificationScheduler.scheduleWotdNotification(hour, minute)
        notificationScheduler.scheduleWotdApi(hour, minute)
    }
}
