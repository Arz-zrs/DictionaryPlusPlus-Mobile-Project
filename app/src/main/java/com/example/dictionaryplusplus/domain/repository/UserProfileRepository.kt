package com.example.dictionaryplusplus.domain.repository

import com.example.dictionaryplusplus.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun observeUserProfile(): Flow<UserProfile?>
    suspend fun getUserProfile(): UserProfile?
    suspend fun updateDisplayName(displayName: String): Result<Unit>
    suspend fun createProfile(uid: String, displayName: String, email: String): Result<UserProfile>
    suspend fun updateLocalScore(points: Int): Result<Unit>
    suspend fun clearLocalProfile()
}
