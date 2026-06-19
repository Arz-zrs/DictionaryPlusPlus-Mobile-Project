package com.example.dictionaryplusplus.data.repository

import com.example.dictionaryplusplus.data.firebase.FirebaseAuthSource
import com.example.dictionaryplusplus.domain.mapper.UserProfile
import com.example.dictionaryplusplus.domain.repository.AuthRepository
import com.example.dictionaryplusplus.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authSource: FirebaseAuthSource,
    private val userRepository: UserRepository
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<UserProfile> {
        return try {
            val authResult = authSource.signInWithEmail(email, password)
            val uid = authResult.getOrThrow()

            val profileResult = userRepository.fetchAndSyncProfile(uid, email)
            profileResult.getOrElse {
                authSource.signOut()
                throw it
            }
            profileResult
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(
        displayName: String,
        email: String,
        password: String
    ): Result<UserProfile> {
        return try {
            val authResult = authSource.signUpWithEmail(email, password)
            val uid = authResult.getOrThrow()

            userRepository.createProfile(uid, displayName, email)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            authSource.signOut()
            userRepository.clearLocalProfile()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isUserSessionActive(): Boolean {
        return authSource.isUserLoggedIn() && userRepository.getUserProfile() != null
    }
}