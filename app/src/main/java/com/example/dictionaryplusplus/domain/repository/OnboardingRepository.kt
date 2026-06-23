package com.example.dictionaryplusplus.domain.repository

import kotlinx.coroutines.flow.Flow

interface OnboardingRepository {
    val hasSeenOnboarding: Flow<Boolean>
    suspend fun completeOnboarding(notificationTime: String)
}
