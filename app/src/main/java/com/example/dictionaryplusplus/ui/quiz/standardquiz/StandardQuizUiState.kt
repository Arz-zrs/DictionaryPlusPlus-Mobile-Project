package com.example.dictionaryplusplus.ui.quiz.standardquiz

import com.example.dictionaryplusplus.util.ErrorMessage
import com.example.dictionaryplusplus.ui.quiz.shared.QuestionState

sealed interface StandardQuizUiState {
    object Loading : StandardQuizUiState
    
    data class Playing(
        val questions: List<QuestionState>,
        val currentIndex: Int,
        val currentQuestionStartTime: Long
    ) : StandardQuizUiState
    
    data class Completed(
        val questions: List<QuestionState>,
        val finalScore: Int
    ) : StandardQuizUiState
    
    data class Error(val errorMessage: ErrorMessage) : StandardQuizUiState
}
