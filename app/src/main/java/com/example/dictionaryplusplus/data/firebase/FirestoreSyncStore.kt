package com.example.dictionaryplusplus.data.firebase

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreSyncStore @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authStore: FirebaseAuthSource
) {
    suspend fun createUserDocument(
        uid: String,
        displayName: String,
        email: String
    ): Result<Unit> {
        return try {
            val userDocument = mapOf(
                "uid" to uid,
                "display_name" to displayName,
                "email" to email,
                "total_score" to 0,
                "seen_words" to emptyList<String>(),
                "favourites" to emptyList<String>(),
                "notes" to emptyList<String>()
            )
            firestore.collection("users").document(uid).set(userDocument).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchUserDocument(
        uid: String
    ): Result<Map<String, Any>?> {
        return try {
            val snapshot = firestore.collection("users").document(uid).get().await()
            Result.success(snapshot.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateScore(
        uid: String,
        displayName: String,
        totalScore: Int
    ): Result<Unit> {
        return try {
            val userUpdates = mapOf(
                "total_score" to totalScore
            )
            firestore.collection("users").document(uid).update(userUpdates).await()
            firestore.collection("leaderboard").document(uid)
                .set(mapOf("uid" to uid, "display_name" to displayName, "total_score" to totalScore), SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncFavouriteChange(word: String, isAdded: Boolean) {
        val uid = authStore.currentUserUid ?: return
        try {
            val updateValue =
                if (isAdded) FieldValue.arrayUnion(word)
                else FieldValue.arrayRemove(word)
            firestore.collection("users").document(uid)
                .update("favourites", updateValue)
                .await()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    suspend fun syncNoteChange(word: String, note: String) {
        val uid = authStore.currentUserUid ?: return
        try {
            firestore.collection("users").document(uid)
                .update("notes.$word", note)
                .await()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    suspend fun syncSeenWordAdded(word: String) {
        val uid = authStore.currentUserUid ?: return
        try {
            firestore.collection("users").document(uid)
                .update("seen_words", FieldValue.arrayUnion(word))
                .await()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    suspend fun updateDisplayName(uid: String, displayName: String): Result<Unit> {
        return try {
            firestore.collection("users").document(uid)
                .update("display_name", displayName)
                .await()
            firestore.collection("leaderboard").document(uid)
                .update("display_name", displayName)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Result.failure(e)
        }
    }
}