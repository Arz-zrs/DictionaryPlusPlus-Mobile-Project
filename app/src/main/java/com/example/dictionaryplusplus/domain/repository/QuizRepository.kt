package com.example.dictionaryplusplus.domain.repository

import kotlinx.coroutines.flow.Flow

interface QuizRepository {
    fun getQuizLength(): Flow<Int>
    suspend fun setQuizLength(length: Int)
    suspend fun saveQuizCompletion(timestamp: Long, refreshTimeSnapshot: String)
    fun getDailyQuizRefreshTime(): Flow<String>
    suspend fun setDailyQuizRefreshTime(time: String)
    fun getLastCompletedAtTimestamp(): Flow<Long?>
    fun getRefreshTimeAtLastCompletion(): Flow<String>
}
