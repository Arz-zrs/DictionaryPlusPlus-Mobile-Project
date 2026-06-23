package com.example.dictionaryplusplus.ui.quiz.synonymquiz

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dictionaryplusplus.core.util.ErrorMessage
import com.example.dictionaryplusplus.ui.quiz.QuizAnswerDisplayState
import com.example.dictionaryplusplus.ui.quiz.QuizQuestionLayout
import com.example.dictionaryplusplus.ui.quiz.QuestionDisplayData

@Composable
fun SynonymQuizScreen(
    onNavigateBack: () -> Unit,
    viewModel: SynonymQuizViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is SynonymQuizUiState.Loading -> CircularProgressIndicator()
                is SynonymQuizUiState.Error -> {
                    val errorText = when (val error = state.errorMessage) {
                        is ErrorMessage.Known -> stringResource(error.messageRes)
                        ErrorMessage.None -> ""
                    }
                    if (errorText.isNotEmpty()) {
                        Text(errorText, color = MaterialTheme.colorScheme.error)
                    }
                }
                is SynonymQuizUiState.Success -> {
                    val displayData = QuestionDisplayData(
                        title = stringResource(state.titleRes),
                        prompt = state.displayWordOrDefinition,
                        choices = state.question.choices,
                        answerState = when (val answer = state.answerState) {
                            is QuizAnswerState.Unanswered -> QuizAnswerDisplayState.Unanswered
                            is QuizAnswerState.Answered -> QuizAnswerDisplayState.Answered(
                                selectedIndex = answer.selectedIndex,
                                isCorrect = answer.isCorrect,
                                correctIndex = state.question.correctAnswerIndex
                            )
                        }
                    )
                    
                    QuizQuestionLayout(
                        data = displayData,
                        onChoiceClick = { viewModel.submitAnswer(it) },
                        onDoneClick = onNavigateBack
                    )
                }
            }
        }
    }
}
