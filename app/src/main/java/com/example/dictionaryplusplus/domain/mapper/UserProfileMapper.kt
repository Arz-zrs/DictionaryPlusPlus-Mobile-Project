package com.example.dictionaryplusplus.domain.mapper

data class UserProfileMapper(
    val userId: String,
    val displayName: String,
    val email: String,
    val totalScore: Int = 0,
)
