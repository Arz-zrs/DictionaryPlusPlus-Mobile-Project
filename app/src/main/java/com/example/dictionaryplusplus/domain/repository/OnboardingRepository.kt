package com.example.dictionaryplusplus.domain.repository

import kotlinx.coroutines.flow.Flow

interface OnboardingRepository {
    val hasSeenOnboarding: Flow<Boolean>
    val notificationTime: Flow<String>
    suspend fun completeOnboarding(notificationTime: String)
    suspend fun updateNotificationTime(notificationTime: String)

}
