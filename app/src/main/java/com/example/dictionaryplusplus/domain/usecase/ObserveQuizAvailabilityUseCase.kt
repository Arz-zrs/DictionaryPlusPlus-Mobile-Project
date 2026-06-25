package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

class ObserveQuizAvailabilityUseCase @Inject constructor(
    private val quizRepository: QuizRepository,
    private val isQuizAvailableUseCase: IsQuizAvailableUseCase
) {
    operator fun invoke(): Flow<Boolean> {
        return combine(
            quizRepository.getLastCompletedAtTimestamp(),
            quizRepository.getRefreshTimeAtLastCompletion()
        ) { lastCompletedAtTimestamp, refreshTimeAtLastCompletionStr ->
            val lastCompletedAt = lastCompletedAtTimestamp?.let {
                LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
            }
            val refreshTimeAtLastCompletion = try {
                LocalTime.parse(refreshTimeAtLastCompletionStr)
            } catch (_: Exception) {
                LocalTime.of(6, 0)
            }
            val now = LocalDateTime.now()

            isQuizAvailableUseCase(
                lastCompletedAt = lastCompletedAt,
                refreshTimeAtLastCompletion = refreshTimeAtLastCompletion,
                now = now
            )
        }
    }
}
