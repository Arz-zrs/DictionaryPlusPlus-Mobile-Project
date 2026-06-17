package com.example.dictionaryplusplus.domain.model

data class UserProfile(
    val userId: String,
    val username: String,
    val email: String,
    val totalScore: Int = 0,
)
