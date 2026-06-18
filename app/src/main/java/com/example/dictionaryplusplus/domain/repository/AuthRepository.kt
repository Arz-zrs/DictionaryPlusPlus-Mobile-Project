package com.example.dictionaryplusplus.domain.repository

import com.example.dictionaryplusplus.domain.mapper.UserProfileMapper

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<UserProfileMapper>
    suspend fun register(displayName: String, email: String, password: String): Result<UserProfileMapper>
    suspend fun logout(): Result<Unit>
    suspend fun isUserSessionActive(): Boolean
}