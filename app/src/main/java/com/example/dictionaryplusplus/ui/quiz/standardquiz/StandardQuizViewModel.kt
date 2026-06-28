package com.example.dictionaryplusplus.ui.quiz.standardquiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryplusplus.R
import com.example.dictionaryplusplus.util.ErrorMessage
import com.example.dictionaryplusplus.domain.usecase.quiz.CompleteStandardQuizUseCase
import com.example.dictionaryplusplus.domain.usecase.quiz.GetStandardQuizUseCase
import com.example.dictionaryplusplus.domain.usecase.quiz.GetQuizLengthUseCase
import com.example.dictionaryplusplus.domain.usecase.quiz.ScoreAnswerUseCase
import com.example.dictionaryplusplus.ui.quiz.shared.AnswerState
import com.example.dictionaryplusplus.ui.quiz.shared.QuestionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StandardQuizViewModel @Inject constructor(
    private val getStandardQuizUseCase: GetStandardQuizUseCase,
    private val getQuizLengthUseCase: GetQuizLengthUseCase,
    private val completeStandardQuizUseCase: CompleteStandardQuizUseCase,
    private val scoreAnswerUseCase: ScoreAnswerUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<StandardQuizUiState>(StandardQuizUiState.Loading)
    val uiState: StateFlow<StandardQuizUiState> = _uiState.asStateFlow()

    private var pausedAtMillis: Long? = null
    private var accumulatedPauseMillis: Long = 0L

    fun onPause() {
        if (pausedAtMillis == null)
            pausedAtMillis = System.currentTimeMillis()
    }

    fun onResume() {
        pausedAtMillis?.let { pausedAt ->
            accumulatedPauseMillis += System.currentTimeMillis() - pausedAt
            pausedAtMillis = null
        }
    }

    fun startQuiz(wordList: List<String> = emptyList()) {
        _uiState.value = StandardQuizUiState.Loading
        viewModelScope.launch {
            val length = getQuizLengthUseCase().first()
            getStandardQuizUseCase(count = length, wordList = wordList)
                .onSuccess { questions ->
                    _uiState.value = StandardQuizUiState.Playing(
                        questions = questions.map { QuestionState(it) },
                        currentIndex = 0,
                        currentQuestionStartTime = System.currentTimeMillis()
                    )
                }
                .onFailure {
                    _uiState.value =
                        StandardQuizUiState.Error(ErrorMessage.Known(R.string.error_unknown))
                }
        }
    }

    fun submitAnswer(index: Int) {
        val currentState = _uiState.value
        if (currentState is StandardQuizUiState.Playing) {
            val currentQuestionState = currentState.questions[currentState.currentIndex]
            if (currentQuestionState.answerState is AnswerState.Answered) return

            val rawElapsed = System.currentTimeMillis() - currentState.currentQuestionStartTime
            val answerTime = (rawElapsed - accumulatedPauseMillis).coerceAtLeast(0L)
            accumulatedPauseMillis = 0L

            val isCorrect = index == currentQuestionState.question.correctAnswerIndex
            val score = scoreAnswerUseCase(isCorrect, answerTime)

            val updatedQuestions = currentState.questions.toMutableList()
            updatedQuestions[currentState.currentIndex] = currentQuestionState.copy(
                answerState = AnswerState.Answered(
                    selectedIndex = index,
                    scoreResult = score
                )
            )

            _uiState.value = currentState.copy(
                questions = updatedQuestions
            )
        }
    }

    fun nextQuestion() {
        val currentState = _uiState.value
        if (currentState is StandardQuizUiState.Playing) {
            if (currentState.currentIndex < (currentState.questions.size - 1)) {
                accumulatedPauseMillis = 0L
                _uiState.value = currentState.copy(
                    currentIndex = currentState.currentIndex + 1,
                    currentQuestionStartTime = System.currentTimeMillis()
                )
            } else {
                val totalScore = currentState.questions.sumOf {
                    (it.answerState as? AnswerState.Answered)?.scoreResult?.totalPoints ?: 0
                }
                
                viewModelScope.launch {
                    completeStandardQuizUseCase(totalScore)
                }

                _uiState.value = StandardQuizUiState.Completed(
                    questions = currentState.questions,
                    finalScore = totalScore
                )
            }
        }
    }
}
