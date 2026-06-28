package com.example.dictionaryplusplus.ui.quiz.shared

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dictionaryplusplus.R
import com.example.dictionaryplusplus.ui.theme.Success

@Composable
fun QuizQuestionLayout(
    data: QuestionDisplayData,
    onChoiceClick: (Int) -> Unit,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = data.title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = data.prompt,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 16.dp),
            textAlign = TextAlign.Center,
            maxLines = 5,
            overflow = TextOverflow.Ellipsis
        )

        data.choices.forEachIndexed { index, choice ->
            val answerState = data.answerState
            val isSelected = (answerState is QuizAnswerDisplayState.Answered) && (answerState.selectedIndex == index)
            val isCorrect = (answerState is QuizAnswerDisplayState.Answered) && (answerState.correctIndex == index)
            val hasAnswered = answerState is QuizAnswerDisplayState.Answered

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
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = choice,
                    textAlign = TextAlign.Center,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis,
                    color = if (hasAnswered && (isCorrect || isSelected)) {
                        if (isCorrect) Success else MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
        }

        if (data.answerState is QuizAnswerDisplayState.Answered) {
            if (data.answerState.showTimeBonus) {
                Text(
                    text = stringResource(R.string.quiz_time_bonus),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onDoneClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.btn_done))
            }
        }
    }
}
