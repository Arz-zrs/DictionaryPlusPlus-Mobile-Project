package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetQuizLengthUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    operator fun invoke(): Flow<Int> {
        return quizRepository.getQuizLength()
    }
}
