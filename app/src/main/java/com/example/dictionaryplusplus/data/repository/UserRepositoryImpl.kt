package com.example.dictionaryplusplus.data.repository

import com.example.dictionaryplusplus.core.di.ApplicationScope
import com.example.dictionaryplusplus.data.firebase.FirestoreSyncStore
import com.example.dictionaryplusplus.data.local.UserPreferences
import com.example.dictionaryplusplus.data.local.dao.FavouriteDao
import com.example.dictionaryplusplus.data.local.dao.SeenEventDao
import com.example.dictionaryplusplus.data.local.dao.UserProfileDao
import com.example.dictionaryplusplus.data.local.dao.WordDao
import com.example.dictionaryplusplus.data.local.dao.WordNoteDao
import com.example.dictionaryplusplus.data.local.entity.FavouriteEntity
import com.example.dictionaryplusplus.data.local.entity.SeenEventEntity
import com.example.dictionaryplusplus.data.local.entity.UserProfileEntity
import com.example.dictionaryplusplus.data.local.entity.WordEntity
import com.example.dictionaryplusplus.data.local.entity.WordNoteEntity
import com.example.dictionaryplusplus.data.local.mapper.toDomain
import com.example.dictionaryplusplus.domain.model.PreferenceConstants
import com.example.dictionaryplusplus.domain.model.UserProfile
import com.example.dictionaryplusplus.domain.repository.UserProfileRepository
import com.example.dictionaryplusplus.domain.repository.UserSyncRepository
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firestoreSource: FirestoreSyncStore,
    private val userProfileDao: UserProfileDao,
    private val seenEventDao: SeenEventDao,
    private val favouriteDao: FavouriteDao,
    private val wordNoteDao: WordNoteDao,
    private val wordDao: WordDao,
    private val userPreferences: UserPreferences,
    @ApplicationScope private val applicationScope: CoroutineScope
) : UserProfileRepository, UserSyncRepository {

    override fun observeUserProfile(): Flow<UserProfile?> {
        return userProfileDao.observeUserProfile().map { it?.toDomain() }
    }

    override suspend fun getUserProfile(): UserProfile? {
        return userProfileDao.getUserProfile()?.toDomain()
    }

    override suspend fun updateDisplayName(displayName: String): Result<Unit> {
        return try {
            val profile = userProfileDao.getUserProfile()
                ?: return Result.failure(Exception("No profile found"))

            userProfileDao.updateDisplayName(profile.userId, displayName)

            applicationScope.launch(Dispatchers.IO) {
                firestoreSource.updateDisplayName(profile.userId, displayName)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Result.failure(e)
        }
    }

    override suspend fun fetchAndSyncProfile(uid: String, email: String): Result<UserProfile> {
        return try {
            val cloudDataResult = firestoreSource.fetchUserDocument(uid)
            val cloudData = cloudDataResult.getOrThrow() ?: throw Exception("User document not found")

            val displayName = cloudData["display_name"] as? String ?: "User"
            val totalScore = (cloudData["total_score"] as? Long)?.toInt() ?: 0

            val lastQuizCompletedAt = (cloudData["last_quiz_completed_at"] as? Long) ?: 0L
            val refreshTimeAtCompletion = (cloudData["refresh_time_at_completion"] as? String) 
                ?: PreferenceConstants.DEFAULT_REFRESH_TIMESTAMP
            
            userPreferences.saveQuizCompletion(lastQuizCompletedAt, refreshTimeAtCompletion)

            val localProfile = UserProfileEntity(
                userId = uid,
                displayName = displayName,
                email = email,
                totalScore = totalScore
            )
            userProfileDao.insertUserProfile(localProfile)

            val favourites = (cloudData["favourites"] as? List<*>)
                ?.filterIsInstance<String>() ?: emptyList()
            favouriteDao.clearAll()
            favourites.forEach { word ->
                favouriteDao.insertFavourite(
                    FavouriteEntity(
                        word = word,
                        addedAtTimestamp = System.currentTimeMillis()
                    )
                )
            }

            @Suppress("UNCHECKED_CAST")
            val notes = (cloudData["notes"] as? Map<String, String>) ?: emptyMap()
            notes.forEach { (word, note) ->
                wordNoteDao.insertWordNote(
                    WordNoteEntity(
                        word = word,
                        note = note,
                        lastUpdated = System.currentTimeMillis()
                    )
                )
            }

            val seenWords = (cloudData["seen_words"] as? List<*>)
                ?.filterIsInstance<String>() ?: emptyList()
            seenEventDao.clearAll()
            seenWords.forEach { word ->
                wordDao.insertWords(listOf(WordEntity(word = word)))
                seenEventDao.insertSeenEvent(
                    SeenEventEntity(
                        word = word,
                        seenAtTimestamp = System.currentTimeMillis(),
                        isConfirmed = true
                    )
                )
            }

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

    override suspend fun clearAllUserData() {
        seenEventDao.clearAll()
        favouriteDao.clearAll()
        wordNoteDao.clearAll()
    }

    override suspend fun updateLocalScore(points: Int): Result<Unit> {
        return try {
            userProfileDao.updateScore(points)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncScoreToCloud(): Result<Unit> {
        return try {
            val profile = userProfileDao.getUserProfile() ?: throw Exception("Profile not found")
            val lastCompletedAt = userPreferences.lastCompletedAtTimestamp.first() ?: 0L
            val refreshTime = userPreferences.refreshTimeAtLastCompletion.first()

            firestoreSource.updateScoreAndQuizCompletion(
                uid = profile.userId,
                displayName = profile.displayName,
                totalScore = profile.totalScore,
                lastCompletedAt = lastCompletedAt,
                refreshTime = refreshTime
            ).getOrThrow()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun restoreQuizStateFromCloud(): Result<Unit> {
        return try {
            val uid = userProfileDao.getUserProfile()?.userId
                ?: firestoreSource.getCurrentUid()
                ?: return Result.success(Unit)

            val cloudData = firestoreSource.fetchUserDocument(uid).getOrThrow()
                ?: return Result.success(Unit)

            val lastQuizCompletedAt = (cloudData["last_quiz_completed_at"] as? Long) ?: 0L
            val refreshTimeAtCompletion = (cloudData["refresh_time_at_completion"] as? String)
                ?: PreferenceConstants.DEFAULT_REFRESH_TIMESTAMP

            userPreferences.saveQuizCompletion(lastQuizCompletedAt, refreshTimeAtCompletion)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}