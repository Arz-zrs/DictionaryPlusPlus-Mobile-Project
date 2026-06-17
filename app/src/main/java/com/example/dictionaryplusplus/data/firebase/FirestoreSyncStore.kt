package com.example.dictionaryplusplus.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreSyncStore @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun createUserDocument(
        uid: String,
        username: String,
        email: String
    ): Result<Unit> {
        return try {
            val userDocument = mapOf(
                "uid" to uid,
                "username" to username,
                "email" to email,
                "totalScore" to 0,
                "last_quiz_completed_at" to null,
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
}