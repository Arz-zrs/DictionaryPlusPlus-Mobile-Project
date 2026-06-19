package com.example.dictionaryplusplus.domain.repository

import com.example.dictionaryplusplus.domain.mapper.UserProfile

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<UserProfile>
    suspend fun register(displayName: String, email: String, password: String): Result<UserProfile>
    suspend fun logout(): Result<Unit>
    suspend fun isUserSessionActive(): Boolean
}