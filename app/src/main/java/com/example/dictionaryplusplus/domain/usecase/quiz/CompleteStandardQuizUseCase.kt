package com.example.dictionaryplusplus.domain.usecase.quiz

import com.example.dictionaryplusplus.domain.repository.ScoreSyncScheduler
import com.example.dictionaryplusplus.domain.repository.UserProfileRepository
import javax.inject.Inject

class CompleteStandardQuizUseCase @Inject constructor(
    private val userRepository: UserProfileRepository,
    private val scoreSyncScheduler: ScoreSyncScheduler
) {
    suspend operator fun invoke(totalPoints: Int): Result<Unit> {
        return try {
            userRepository.updateLocalScore(totalPoints).getOrThrow()
            scoreSyncScheduler.scheduleSync()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
