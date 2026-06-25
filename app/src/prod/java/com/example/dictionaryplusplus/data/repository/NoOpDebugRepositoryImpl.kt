package com.example.dictionaryplusplus.data.repository

import com.example.dictionaryplusplus.domain.repository.DebugRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoOpDebugRepositoryImpl @Inject constructor() : DebugRepository {
    override fun triggerWotdWorker() {
        // No-op for prod
    }

    override fun triggerDailyWordWorker() {
        // No-op for prod
    }

    override suspend fun resetQuizCompletion() {
        // No-op for prod
    }
}
