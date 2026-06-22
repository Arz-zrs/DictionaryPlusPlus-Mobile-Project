package com.example.dictionaryplusplus.domain.repository

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<String>
    suspend fun register(email: String, password: String): Result<String>
    suspend fun logout(): Result<Unit>
    suspend fun isUserSessionActive(): Boolean
}