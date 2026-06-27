package com.example.dictionaryplusplus.domain.usecase.quiz

import com.example.dictionaryplusplus.domain.repository.QuizRepository
import javax.inject.Inject

class SetDailyQuizRefreshTimeUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend operator fun invoke(time: String) {
        quizRepository.setDailyQuizRefreshTime(time)
    }
}