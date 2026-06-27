package com.example.dictionaryplusplus.domain.usecase.setting

import com.example.dictionaryplusplus.domain.repository.NotificationScheduler
import com.example.dictionaryplusplus.domain.repository.OnboardingRepository
import java.util.Locale
import javax.inject.Inject


class RescheduleWotdUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
    private val notificationScheduler: NotificationScheduler
) {
    suspend operator fun invoke(hour: Int, minute: Int) {
        val formatted = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
        onboardingRepository.updateNotificationTime(formatted)
        notificationScheduler.scheduleWotdNotification(hour, minute)
    }
}