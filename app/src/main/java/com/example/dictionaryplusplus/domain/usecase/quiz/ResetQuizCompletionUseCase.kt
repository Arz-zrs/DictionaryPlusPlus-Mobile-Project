package com.example.dictionaryplusplus.domain.usecase.quiz

import com.example.dictionaryplusplus.domain.repository.DebugRepository
import javax.inject.Inject

class ResetQuizCompletionUseCase @Inject constructor(
    private val debugRepository: DebugRepository
) {
    suspend operator fun invoke() {
        debugRepository.resetQuizCompletion()
    }
}
