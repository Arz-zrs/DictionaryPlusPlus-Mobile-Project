package com.example.dictionaryplusplus.domain.repository

interface DebugRepository {
    fun triggerWotdWorker()
    fun triggerDailyWordWorker()
    suspend fun resetQuizCompletion()
}
