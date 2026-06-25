package com.example.dictionaryplusplus.data.repository

import com.example.dictionaryplusplus.data.local.UserPreferences
import com.example.dictionaryplusplus.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepositoryImpl @Inject constructor(
    private val userPreferences: UserPreferences
) : QuizRepository {
    override fun getQuizLength(): Flow<Int> = userPreferences.quizLength

    override suspend fun setQuizLength(length: Int) = userPreferences.setQuizLength(length)

    override suspend fun saveQuizCompletion(timestamp: Long, refreshTimeSnapshot: String) {
        userPreferences.saveQuizCompletion(timestamp, refreshTimeSnapshot)
    }

    override fun getDailyQuizRefreshTime(): Flow<String> = userPreferences.dailyQuizRefreshTime

    override suspend fun setDailyQuizRefreshTime(time: String) = userPreferences.setDailyQuizRefreshTime(time)

    override fun getLastCompletedAtTimestamp(): Flow<Long?> = userPreferences.lastCompletedAtTimestamp

    override fun getRefreshTimeAtLastCompletion(): Flow<String> = userPreferences.refreshTimeAtLastCompletion
}
