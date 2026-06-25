package com.example.dictionaryplusplus.data.repository

import com.example.dictionaryplusplus.data.local.UserPreferences
import com.example.dictionaryplusplus.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingRepositoryImpl @Inject constructor(
    private val userPreferences: UserPreferences
) : OnboardingRepository {
    override val hasSeenOnboarding: Flow<Boolean> = userPreferences.hasSeenOnboarding

    override suspend fun completeOnboarding(notificationTime: String) {
        userPreferences.setNotificationTime(notificationTime)
        userPreferences.setHasSeenOnboarding(true)
    }

    override val notificationTime: Flow<String> = userPreferences.notificationTime

    override suspend fun updateNotificationTime(notificationTime: String) {
        userPreferences.setNotificationTime(notificationTime)
    }
}
