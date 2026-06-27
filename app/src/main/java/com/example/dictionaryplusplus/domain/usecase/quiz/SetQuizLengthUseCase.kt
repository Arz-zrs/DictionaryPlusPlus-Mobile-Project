package com.example.dictionaryplusplus.domain.usecase.quiz

import com.example.dictionaryplusplus.domain.repository.QuizRepository
import javax.inject.Inject

class SetQuizLengthUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend operator fun invoke(length: Int) {
        quizRepository.setQuizLength(length)
    }
}
