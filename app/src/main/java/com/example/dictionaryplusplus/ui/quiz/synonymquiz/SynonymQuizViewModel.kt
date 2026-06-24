package com.example.dictionaryplusplus.ui.quiz.synonymquiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryplusplus.R
import com.example.dictionaryplusplus.domain.model.QuizQuestion
import com.example.dictionaryplusplus.domain.usecase.GetSynonymQuizUseCase
import com.example.dictionaryplusplus.core.util.asErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SynonymQuizViewModel @Inject constructor(
    private val getSynonymQuizUseCase: GetSynonymQuizUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _uiState = MutableStateFlow<SynonymQuizUiState>(SynonymQuizUiState.Loading)
    val uiState: StateFlow<SynonymQuizUiState> = _uiState.asStateFlow()

    init {
        val word: String? = savedStateHandle["word"]
        loadQuiz(word)
    }

    fun loadQuiz(word: String?) {
        _uiState.value = SynonymQuizUiState.Loading
        viewModelScope.launch {
            getSynonymQuizUseCase(word)
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
        return if (question.isFallbackToDefinition) {
            SynonymQuizUiState.Success(
                question = question,
                titleRes = R.string.quiz_find_definition,
                displayWordOrDefinition = question.originalDefinition
            )
        } else {
            SynonymQuizUiState.Success(
                question = question,
                titleRes = R.string.quiz_find_synonym,
                displayWordOrDefinition = question.word.uppercase()
            )
        }
    }
}
