package com.example.dictionaryplusplus.domain.repository

import kotlinx.coroutines.flow.Flow

interface QuizRepository {
    fun getQuizLength(): Flow<Int>
    suspend fun setQuizLength(length: Int)
}
