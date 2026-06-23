package com.example.dictionaryplusplus.ui.quiz.synonymquiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryplusplus.R
import com.example.dictionaryplusplus.domain.model.QuizQuestion
import com.example.dictionaryplusplus.domain.usecase.GetSynonymQuizUC
import com.example.dictionaryplusplus.util.asErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SynonymQuizViewModel @Inject constructor(
    private val getSynonymQuizUC: GetSynonymQuizUC,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _uiState = MutableStateFlow<SynonymQuizUiState>(SynonymQuizUiState.Loading)
    val uiState: StateFlow<SynonymQuizUiState> = _uiState.asStateFlow()

    init {
        val word: String? = savedStateHandle["word"]
        loadQuiz(word)
    }

    fun loadQuiz(word: String?) {
        val anchorWord = word ?: "abandon"
        _uiState.value = SynonymQuizUiState.Loading
        viewModelScope.launch {
            getSynonymQuizUC(anchorWord)
                .onSuccess { question ->
                    _uiState.value = mapToSuccessState(question)
                }
                .onFailure {
                    _uiState.value = SynonymQuizUiState.Error(asErrorMessage(R.string.error_unknown))
                }
        }
    }

    fun submitAnswer(index: Int) {
        _uiState.update { state ->
            if (state is SynonymQuizUiState.Success && state.answerState is QuizAnswerState.Unanswered) {
                state.copy(
                    answerState = QuizAnswerState.Answered(
                        selectedIndex = index,
                        isCorrect = index == state.question.correctAnswerIndex
                    )
                )
            } else {
                state
            }
        }
    }

    private fun mapToSuccessState(question: QuizQuestion): SynonymQuizUiState.Success {
        return SynonymQuizUiState.Success(
            question = question,
            titleRes = if (question.isFallbackToDefinition) {
                R.string.quiz_match_definition
            } else {
                R.string.quiz_find_synonym
            },
            displayWordOrDefinition = if (question.isFallbackToDefinition) {
                "\"${question.originalDefinition}\""
            } else {
                question.word.uppercase()
            }
        )
    }
}
