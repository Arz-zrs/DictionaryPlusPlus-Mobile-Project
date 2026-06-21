package com.example.dictionaryplusplus.domain.model

data class UserProfile(
    val userId: String,
    val displayName: String,
    val email: String,
    val totalScore: Int = 0,
)
