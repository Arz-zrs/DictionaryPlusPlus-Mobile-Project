package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.domain.repository.NotificationScheduler
import com.example.dictionaryplusplus.domain.repository.OnboardingRepository
import javax.inject.Inject

// TODO use this for Settings on NotifTimePicker save
class RescheduleDailyWordUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
    private val notificationScheduler: NotificationScheduler
) {
    suspend operator fun invoke(hour: Int, minute: Int) {
        notificationScheduler.scheduleDailyWord(hour, minute)
    }
}