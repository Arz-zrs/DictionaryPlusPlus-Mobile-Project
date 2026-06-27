package com.example.dictionaryplusplus.domain.usecase.quiz

import com.example.dictionaryplusplus.domain.repository.QuizRepository
import javax.inject.Inject

class GetDailyQuizRefreshTimeUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    operator fun invoke() = quizRepository.getDailyQuizRefreshTime()
}