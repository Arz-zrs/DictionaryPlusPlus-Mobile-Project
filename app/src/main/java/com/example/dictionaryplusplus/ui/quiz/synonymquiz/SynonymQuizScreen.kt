package com.example.dictionaryplusplus.ui.quiz.synonymquiz

import androidx.compose.foundation.BorderStroke
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
import com.example.dictionaryplusplus.R
import com.example.dictionaryplusplus.ui.theme.Success
import com.example.dictionaryplusplus.core.util.ErrorMessage

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
                    SynonymQuizLayout(
                        state = state,
                        onChoiceClick = { viewModel.submitAnswer(it) },
                        onDoneClick = onNavigateBack
                    )
                }
            }
        }
    }
}

@Composable
fun SynonymQuizLayout(
    state: SynonymQuizUiState.Success,
    onChoiceClick: (Int) -> Unit,
    onDoneClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(state.titleRes),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = state.displayWordOrDefinition,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        state.question.choices.forEachIndexed { index, choice ->
            val answerState = state.answerState
            val isSelected = answerState is QuizAnswerState.Answered && answerState.selectedIndex == index
            val isCorrect = index == state.question.correctAnswerIndex
            val hasAnswered = answerState is QuizAnswerState.Answered

            val containerColor = when {
                !hasAnswered -> MaterialTheme.colorScheme.surface
                isCorrect -> Success.copy(alpha = 0.15f)
                isSelected -> MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                else -> MaterialTheme.colorScheme.surface
            }

            val borderColor = when {
                !hasAnswered -> MaterialTheme.colorScheme.outline
                isCorrect -> Success
                isSelected -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            }

            OutlinedButton(
                onClick = { onChoiceClick(index) },
                enabled = !hasAnswered,
                colors = ButtonDefaults.outlinedButtonColors(containerColor = containerColor),
                border = BorderStroke(1.5.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = choice,
                    color = if (hasAnswered && (isCorrect || isSelected)) {
                        if (isCorrect) Success else MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        if (state.answerState is QuizAnswerState.Answered) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onDoneClick, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.quiz_back_to_hub))
            }
        }
    }
}
