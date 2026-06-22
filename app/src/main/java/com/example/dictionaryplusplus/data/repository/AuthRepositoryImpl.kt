package com.example.dictionaryplusplus.data.repository

import com.example.dictionaryplusplus.data.firebase.FirebaseAuthSource
import com.example.dictionaryplusplus.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authSource: FirebaseAuthSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<String> {
        return authSource.signInWithEmail(email, password)
    }

    override suspend fun register(email: String, password: String): Result<String> {
        return authSource.signUpWithEmail(email, password)
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            authSource.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isUserSessionActive(): Boolean {
        return authSource.isUserLoggedIn()
    }
}