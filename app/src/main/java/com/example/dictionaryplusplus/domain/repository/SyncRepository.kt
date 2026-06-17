package com.example.dictionaryplusplus.domain.repository

import com.example.dictionaryplusplus.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface SyncRepository {
    suspend fun login(email: String, password: String): Result<UserProfile>
    suspend fun register(username: String, email: String, password: String): Result<UserProfile>
    suspend fun logout(): Result<Unit>
    fun observeUserProfile(): Flow<UserProfile?>
    suspend fun isUserSessionActive(): Boolean
}