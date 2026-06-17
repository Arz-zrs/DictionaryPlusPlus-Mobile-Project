package com.example.dictionaryplusplus.domain.repository

import com.example.dictionaryplusplus.data.firebase.FirebaseAuthSource
import com.example.dictionaryplusplus.data.firebase.FirestoreSyncStore
import com.example.dictionaryplusplus.data.local.dao.UserProfileDao
import com.example.dictionaryplusplus.data.local.entity.UserProfileEntity
import com.example.dictionaryplusplus.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepositoryImpl @Inject constructor(
    private val authSource: FirebaseAuthSource,
    private val firestoreSource: FirestoreSyncStore,
    private val userProfileDao: UserProfileDao
): SyncRepository {
    override suspend fun login(
        email: String,
        password: String
    ): Result<UserProfile> {
        return try {
            val authResult = authSource.signInWithEmail(email, password)
            val uid = authResult.getOrThrow()

            val cloudDataResult = firestoreSource.fetchUserDocument(uid)
            val cloudData = cloudDataResult.getOrThrow() ?: throw Exception("User document not found")

            val username = cloudData["username"] as? String ?: "User"
            val totalScore = (cloudData["totalScore"] as? Long)?.toInt() ?: 0

            val localProfile = UserProfileEntity(
                userId = uid,
                username = username,
                email = email,
                totalScore = totalScore
            )
            userProfileDao.insertUserProfile(localProfile)
            Result.success(localProfile.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(
        username: String,
        email: String,
        password: String
    ): Result<UserProfile> {
        return try {
            val authResult = authSource.signUpWithEmail(email, password)
            val uid = authResult.getOrThrow()

            firestoreSource.createUserDocument(uid, username, email).getOrThrow()
            val localProfile = UserProfileEntity(
                userId = uid,
                username = username,
                email = email,
                totalScore = 0
            )
            userProfileDao.insertUserProfile(localProfile)
            Result.success(localProfile.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            authSource.signOut()
            userProfileDao.clearUserProfile()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeUserProfile(): Flow<UserProfile?> {
        return userProfileDao.observeUserProfile().map { it?.toDomain() }
    }

    override suspend fun isUserSessionActive(): Boolean {
        return authSource.isUserLoggedIn() && userProfileDao.getUserProfile() != null
    }

    private fun UserProfileEntity.toDomain() = UserProfile(
        userId = userId,
        username = username,
        email = email,
        totalScore = totalScore,
    )
}