package com.example.dictionaryplusplus.domain.repository

import com.example.dictionaryplusplus.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeUserProfile(): Flow<UserProfile?>
    suspend fun getUserProfile(): UserProfile?
    suspend fun fetchAndSyncProfile(uid: String, email: String): Result<UserProfile>
    suspend fun createProfile(uid: String, displayName: String, email: String): Result<UserProfile>
    suspend fun clearLocalProfile()
}