package com.example.dictionaryplusplus.domain.repository

import com.example.dictionaryplusplus.domain.mapper.UserProfileMapper
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeUserProfile(): Flow<UserProfileMapper?>
    suspend fun getUserProfile(): UserProfileMapper?
    suspend fun fetchAndSyncProfile(uid: String, email: String): Result<UserProfileMapper>
    suspend fun createProfile(uid: String, displayName: String, email: String): Result<UserProfileMapper>
    suspend fun clearLocalProfile()
}