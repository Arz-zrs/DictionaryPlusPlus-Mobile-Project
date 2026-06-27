package com.example.dictionaryplusplus.domain.usecase.quiz

import com.example.dictionaryplusplus.domain.model.AnswerScoreResult
import javax.inject.Inject

class ScoreAnswerUseCase @Inject constructor() {
    operator fun invoke(isCorrect: Boolean, answerTimeMillis: Long): AnswerScoreResult {
        if (!isCorrect) return AnswerScoreResult(0, 0, 0)

        val basePoints = 10
        val speedBonus = if (answerTimeMillis <= 5000) 5 else 0

        return AnswerScoreResult(
            basePoints = basePoints,
            speedBonus = speedBonus,
            totalPoints = basePoints + speedBonus
        )
    }
}