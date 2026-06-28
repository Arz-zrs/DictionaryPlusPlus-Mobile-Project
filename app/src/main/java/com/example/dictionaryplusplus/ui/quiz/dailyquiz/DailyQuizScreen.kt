package com.example.dictionaryplusplus.ui.quiz.dailyquiz

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.dictionaryplusplus.ui.quiz.shared.QuizQuestionLayout
import com.example.dictionaryplusplus.ui.quiz.shared.QuizSummaryCard
import com.example.dictionaryplusplus.ui.quiz.shared.AnswerState
import com.example.dictionaryplusplus.ui.quiz.shared.QuestionDisplayData
import com.example.dictionaryplusplus.ui.quiz.shared.QuizAnswerDisplayState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyQuizScreen(
    onNavigateBack: () -> Unit,
    viewModel: DailyQuizViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showQuitDialog by remember { mutableStateOf(false) }
    val isPlaying = uiState is DailyQuizUiState.Playing

    val lifecycleOwner = LocalLifecycleOwner.current

    fun requestQuit() {
        if (isPlaying) {
            viewModel.onPause()
            showQuitDialog = true
        } else {
            onNavigateBack()
        }
    }

    BackHandler(enabled = isPlaying) {
        requestQuit()
    }

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
        if (viewModel.uiState.value is DailyQuizUiState.Loading) {
            viewModel.startQuiz()
        }
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
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(errorText, color = MaterialTheme.colorScheme.error)
                            Button(onClick = { viewModel.startQuiz() }) {
                                Text(stringResource(R.string.btn_retry))
                            }
                        }
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

                        val quizAnswerDisplayState =
                            when (val quizAnswer = currentQuestionState.answerState) {
                            is AnswerState.Unanswered -> QuizAnswerDisplayState.Unanswered
                            is AnswerState.Answered -> {
                                QuizAnswerDisplayState.Answered(
                                    selectedIndex = quizAnswer.selectedIndex,
                                    isCorrect = quizAnswer.selectedIndex == currentQuestionState.question.correctAnswerIndex,
                                    correctIndex = currentQuestionState.question.correctAnswerIndex,
                                    showTimeBonus = quizAnswer.scoreResult.speedBonus > 0
                                )
                            }
                        }

                        val displayData = QuestionDisplayData(
                            title = stringResource(
                                R.string.label_question_count,
                                state.currentIndex + 1,
                                state.questions.size
                            ),
                            prompt = currentQuestionState.question.word.uppercase(),
                            choices = currentQuestionState.question.choices,
                            answerState = quizAnswerDisplayState
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
        IconButton(
            onClick = { requestQuit() },
            modifier = Modifier
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.btn_back)
            )
        }
    }
    if (showQuitDialog) {
        AlertDialog(
            onDismissRequest = {
                showQuitDialog = false
                viewModel.onResume()
            },
            title = { Text(stringResource(R.string.quiz_quit_title)) },
            text = { Text(stringResource(R.string.quiz_quit_desc)) },
            confirmButton = {
                TextButton(onClick = {
                    showQuitDialog = false
                    onNavigateBack()
                }) {
                    Text(stringResource(R.string.quiz_quit_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showQuitDialog = false
                        viewModel.onResume()
                    }
                ) {
                    Text(stringResource(R.string.quiz_quit_cancel))
                }
            }
        )
    }
}
