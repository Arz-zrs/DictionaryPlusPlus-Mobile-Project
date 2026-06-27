package com.example.dictionaryplusplus.ui.quiz.dailyquiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryplusplus.R
import com.example.dictionaryplusplus.core.util.ErrorMessage
import com.example.dictionaryplusplus.domain.usecase.CompleteDailyQuizUseCase
import com.example.dictionaryplusplus.domain.usecase.GetDailyQuizUseCase
import com.example.dictionaryplusplus.domain.usecase.GetQuizLengthUseCase
import com.example.dictionaryplusplus.domain.usecase.ScoreAnswerUseCase
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
class DailyQuizViewModel @Inject constructor(
    private val getDailyQuizUseCase: GetDailyQuizUseCase,
    private val getQuizLengthUseCase: GetQuizLengthUseCase,
    private val completeDailyQuizUseCase: CompleteDailyQuizUseCase,
    private val scoreAnswerUseCase: ScoreAnswerUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DailyQuizUiState>(DailyQuizUiState.Loading)
    val uiState: StateFlow<DailyQuizUiState> = _uiState.asStateFlow()

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
        _uiState.value = DailyQuizUiState.Loading
        viewModelScope.launch {
            val length = getQuizLengthUseCase().first()
            getDailyQuizUseCase(count = length, wordList = wordList)
                .onSuccess { questions ->
                    _uiState.value = DailyQuizUiState.Playing(
                        questions = questions.map { QuestionState(it) },
                        currentIndex = 0,
                        currentQuestionStartTime = System.currentTimeMillis()
                    )
                }
                .onFailure {
                    _uiState.value = DailyQuizUiState.Error(ErrorMessage.Known(R.string.error_unknown))
                }
        }
    }

    fun submitAnswer(index: Int) {
        val currentState = _uiState.value
        if (currentState is DailyQuizUiState.Playing) {
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
        if (currentState is DailyQuizUiState.Playing) {
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
                    completeDailyQuizUseCase(totalScore)
                }

                _uiState.value = DailyQuizUiState.Completed(
                    questions = currentState.questions,
                    finalScore = totalScore
                )
            }
        }
    }
}
