package com.example.dictionaryplusplus.ui.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dictionaryplusplus.R
import com.example.dictionaryplusplus.ui.components.DailyQuizEntryCard

@Composable
fun QuizScreen(
    onNavigateToPracticeQuiz: () -> Unit,
    onNavigateToDailyQuiz: () -> Unit,
    viewModel: QuizHubViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.quiz_hub_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = onNavigateToPracticeQuiz,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.quiz_practice_title),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.quiz_practice_desc),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        DailyQuizEntryCard(
            isAvailable = uiState.isDailyQuizAvailable,
            onStartClick = onNavigateToDailyQuiz
        )
    }
}
