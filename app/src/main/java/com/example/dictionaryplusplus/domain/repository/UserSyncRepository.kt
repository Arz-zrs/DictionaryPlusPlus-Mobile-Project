package com.example.dictionaryplusplus.domain.repository

import com.example.dictionaryplusplus.domain.model.UserProfile

interface UserSyncRepository {
    suspend fun fetchAndSyncProfile(uid: String, email: String): Result<UserProfile>
    suspend fun syncScoreToCloud(): Result<Unit>
    suspend fun restoreQuizStateFromCloud(): Result<Unit>
    suspend fun clearAllUserData()
}
