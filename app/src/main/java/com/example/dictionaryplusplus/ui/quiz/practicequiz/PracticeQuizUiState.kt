package com.example.dictionaryplusplus.ui.quiz.practicequiz

import androidx.annotation.StringRes
import com.example.dictionaryplusplus.domain.model.QuizQuestion
import com.example.dictionaryplusplus.core.util.ErrorMessage

sealed interface PracticeQuizUiState {
    data object Loading: PracticeQuizUiState
    
    data class Success(
        val question: QuizQuestion,
        val answerState: QuizAnswerState = QuizAnswerState.Unanswered,
        @StringRes val titleRes: Int,
        val displayWordOrDefinition: String
    ): PracticeQuizUiState
    
    data class Error(val errorMessage: ErrorMessage): PracticeQuizUiState
}
