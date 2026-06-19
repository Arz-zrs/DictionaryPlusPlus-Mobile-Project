package com.example.dictionaryplusplus.data.repository

import com.example.dictionaryplusplus.data.firebase.FirestoreSyncStore
import com.example.dictionaryplusplus.data.local.dao.UserProfileDao
import com.example.dictionaryplusplus.data.local.entity.UserProfileEntity
import com.example.dictionaryplusplus.domain.mapper.UserProfile
import com.example.dictionaryplusplus.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firestoreSource: FirestoreSyncStore,
    private val userProfileDao: UserProfileDao
) : UserRepository {

    override fun observeUserProfile(): Flow<UserProfile?> {
        return userProfileDao.observeUserProfile().map { it?.toDomain() }
    }

    override suspend fun getUserProfile(): UserProfile? {
        return userProfileDao.getUserProfile()?.toDomain()
    }

    override suspend fun fetchAndSyncProfile(uid: String, email: String): Result<UserProfile> {
        return try {
            val cloudDataResult = firestoreSource.fetchUserDocument(uid)
            val cloudData = cloudDataResult.getOrThrow() ?: throw Exception("User document not found")

            val displayName = cloudData["display_name"] as? String ?: "User"
            val totalScore = (cloudData["total_score"] as? Long)?.toInt() ?: 0

            val localProfile = UserProfileEntity(
                userId = uid,
                displayName = displayName,
                email = email,
                totalScore = totalScore
            )
            userProfileDao.insertUserProfile(localProfile)
            Result.success(localProfile.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createProfile(
        uid: String,
        displayName: String,
        email: String
    ): Result<UserProfile> {
        return try {
            firestoreSource.createUserDocument(uid, displayName, email).getOrThrow()
            val localProfile = UserProfileEntity(
                userId = uid,
                displayName = displayName,
                email = email,
                totalScore = 0
            )
            userProfileDao.insertUserProfile(localProfile)
            Result.success(localProfile.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearLocalProfile() {
        userProfileDao.clearUserProfile()
    }

    private fun UserProfileEntity.toDomain() = UserProfile(
        userId = userId,
        displayName = displayName,
        email = email,
        totalScore = totalScore,
    )
}