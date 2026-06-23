package com.example.dictionaryplusplus.ui.quiz.dailyquiz

import com.example.dictionaryplusplus.core.util.ErrorMessage

sealed interface DailyQuizUiState {
    object Loading : DailyQuizUiState
    
    data class Playing(
        val questions: List<QuestionState>,
        val currentIndex: Int,
        val currentQuestionStartTime: Long
    ) : DailyQuizUiState
    
    data class Completed(
        val questions: List<QuestionState>,
        val finalScore: Int
    ) : DailyQuizUiState
    
    data class Error(val errorMessage: ErrorMessage) : DailyQuizUiState
}
