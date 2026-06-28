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
}
