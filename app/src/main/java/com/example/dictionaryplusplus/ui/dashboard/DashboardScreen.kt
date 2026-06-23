package com.example.dictionaryplusplus.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.dictionaryplusplus.ui.components.LeaderboardPreviewCard
import com.example.dictionaryplusplus.ui.components.ScoreBanner
import com.example.dictionaryplusplus.ui.components.WordOfTheDayCard
import com.example.dictionaryplusplus.ui.dictionary.WordDetailSheet
import com.example.dictionaryplusplus.ui.history.WordHistoryItem

@Composable
fun DashboardScreen(
    onNavigateToQuizHub: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.dashboard_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            ScoreBanner(
                score = uiState.userScore,
                onBannerClick = onNavigateToLeaderboard
            )
        }

        when (val wotd = uiState.wordOfTheDay) {
            WotdState.Loading -> {
                item {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            WotdState.Unavailable -> {
                item {
                    Text(
                        text = stringResource(R.string.dashboard_wotd_unavailable),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            is WotdState.Available -> {
                item {
                    WordOfTheDayCard(
                        wotd = wotd.definition,
                        onCardClick = { viewModel.onWotdClicked(wotd.definition.word) }
                    )
                }
            }
        }

        item {
            DailyQuizEntryCard(
                isAvailable = uiState.isQuizAvailable,
                onStartClick = onNavigateToQuizHub
            )
        }

        item {
            LeaderboardPreviewCard(
                onNavigateToLeaderboard = onNavigateToLeaderboard
            )
        }

        item {
            Text(
                text = stringResource(R.string.label_recent_words),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (uiState.recentWords.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.dashboard_no_recent_words),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        } else {
            items(uiState.recentWords, key = { it.id }) { event ->
                WordHistoryItem(event = event)
            }
        }
    }

    uiState.selectedWotd?.let { word ->
        WordDetailSheet(
            word = word,
            onDismiss = { viewModel.onSheetDismissed() }
        )
    }
}
