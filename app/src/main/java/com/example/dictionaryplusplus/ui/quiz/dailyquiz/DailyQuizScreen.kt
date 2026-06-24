package com.example.dictionaryplusplus.ui.quiz.dailyquiz

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dictionaryplusplus.R
import com.example.dictionaryplusplus.core.util.ErrorMessage
import com.example.dictionaryplusplus.ui.components.QuizProgressBar
import com.example.dictionaryplusplus.ui.quiz.*

@Composable
fun DailyQuizScreen(
    onNavigateBack: () -> Unit,
    viewModel: DailyQuizViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> viewModel.onPause()
                Lifecycle.Event.ON_RESUME -> viewModel.onResume()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        viewModel.startQuiz()
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is DailyQuizUiState.Loading -> CircularProgressIndicator()
                is DailyQuizUiState.Error -> {
                    val errorText = when (val error = state.errorMessage) {
                        is ErrorMessage.Known -> stringResource(error.messageRes)
                        ErrorMessage.None -> ""
                    }
                    if (errorText.isNotEmpty()) {
                        Text(errorText, color = MaterialTheme.colorScheme.error)
                    }
                }
                is DailyQuizUiState.Playing -> {
                    val currentQuestionState = state.questions[state.currentIndex]
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        QuizProgressBar(
                            currentIndex = state.currentIndex,
                            totalSteps = state.questions.size,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        val displayData = QuestionDisplayData(
                            title = stringResource(
                                R.string.label_question_count,
                                state.currentIndex + 1,
                                state.questions.size
                            ),
                            prompt = currentQuestionState.question.word.uppercase(),
                            choices = currentQuestionState.question.choices,
                            answerState = if (currentQuestionState.selectedIndex == null) {
                                QuizAnswerDisplayState.Unanswered
                            } else {
                                QuizAnswerDisplayState.Answered(
                                    selectedIndex = currentQuestionState.selectedIndex,
                                    isCorrect = currentQuestionState.selectedIndex == currentQuestionState.question.correctAnswerIndex,
                                    correctIndex = currentQuestionState.question.correctAnswerIndex
                                )
                            }
                        )

                        QuizQuestionLayout(
                            data = displayData,
                            onChoiceClick = { viewModel.submitAnswer(it) },
                            onDoneClick = { viewModel.nextQuestion() }
                        )
                    }
                }
                is DailyQuizUiState.Completed -> {
                    QuizSummaryCard(
                        finalScore = state.finalScore,
                        questions = state.questions,
                        onDoneClick = onNavigateBack
                    )
                }
            }
        }
    }
}
