package com.example.dictionaryplusplus.domain.usecase.quiz

import com.example.dictionaryplusplus.domain.repository.QuizRepository
import com.example.dictionaryplusplus.domain.repository.ScoreSyncScheduler
import com.example.dictionaryplusplus.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CompleteDailyQuizUseCase @Inject constructor(
    private val quizRepository: QuizRepository,
    private val userRepository: UserProfileRepository,
    private val scoreSyncScheduler: ScoreSyncScheduler
) {
    suspend operator fun invoke(totalPoints: Int): Result<Unit> {
        return try {
            userRepository.updateLocalScore(totalPoints).getOrThrow()

            val currentTimestamp = System.currentTimeMillis()
            val currentRefreshTime = quizRepository.getDailyQuizRefreshTime().first()
            quizRepository.saveQuizCompletion(currentTimestamp, currentRefreshTime)

            scoreSyncScheduler.scheduleSync()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
