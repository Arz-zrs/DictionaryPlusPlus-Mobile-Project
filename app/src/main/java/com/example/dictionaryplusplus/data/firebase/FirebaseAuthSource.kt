package com.example.dictionaryplusplus.data.firebase

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    val currentUserUid: String?
        get() = firebaseAuth.currentUser?.uid

    val currentUserEmail: String?
        get() = firebaseAuth.currentUser?.email

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    suspend fun signInWithEmail(
        email: String,
        password: String
    ): Result<String> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("User ID not found")
            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(
        email: String,
        password: String
    ): Result<String> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("Failed to generate user ID")
            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut(){
        firebaseAuth.signOut()
    }
}