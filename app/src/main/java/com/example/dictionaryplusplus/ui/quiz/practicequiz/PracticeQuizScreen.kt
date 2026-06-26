package com.example.dictionaryplusplus.ui.quiz.practicequiz

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dictionaryplusplus.R
import com.example.dictionaryplusplus.core.util.ErrorMessage
import com.example.dictionaryplusplus.ui.quiz.QuizAnswerDisplayState
import com.example.dictionaryplusplus.ui.quiz.QuizQuestionLayout
import com.example.dictionaryplusplus.ui.quiz.QuestionDisplayData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeQuizScreen(
    onNavigateBack: () -> Unit,
    viewModel: PracticeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp)
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.btn_back)
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (val state = uiState) {
                    is PracticeQuizUiState.Loading -> CircularProgressIndicator()
                    is PracticeQuizUiState.Error -> {
                        val errorText = when (val error = state.errorMessage) {
                            is ErrorMessage.Known -> stringResource(error.messageRes)
                            ErrorMessage.None -> ""
                        }
                        if (errorText.isNotEmpty()) {
                            Text(
                                text = errorText,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    is PracticeQuizUiState.Success -> {
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
}
