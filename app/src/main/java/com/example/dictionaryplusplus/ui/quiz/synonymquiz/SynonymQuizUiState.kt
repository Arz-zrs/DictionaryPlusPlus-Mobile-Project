package com.example.dictionaryplusplus.ui.quiz.synonymquiz

import androidx.annotation.StringRes
import com.example.dictionaryplusplus.domain.model.QuizQuestion
import com.example.dictionaryplusplus.core.util.ErrorMessage

sealed interface SynonymQuizUiState {
    data object Loading: SynonymQuizUiState
    
    data class Success(
        val question: QuizQuestion,
        val answerState: QuizAnswerState = QuizAnswerState.Unanswered,
        @StringRes val titleRes: Int,
        val displayWordOrDefinition: String
    ): SynonymQuizUiState
    
    data class Error(val errorMessage: ErrorMessage): SynonymQuizUiState
}
