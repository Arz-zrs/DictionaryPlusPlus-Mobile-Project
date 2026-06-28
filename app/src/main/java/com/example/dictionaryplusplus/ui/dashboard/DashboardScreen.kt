package com.example.dictionaryplusplus.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.dictionaryplusplus.ui.components.StandardQuizEntryCard
import com.example.dictionaryplusplus.ui.components.ScoreBanner
import com.example.dictionaryplusplus.ui.components.WordOfTheDayCard
import com.example.dictionaryplusplus.ui.dictionary.WordDetailSheet
import com.example.dictionaryplusplus.ui.history.WordHistoryItem

@Composable
fun DashboardScreen(
    onNavigateToQuizHub: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToWordHistory: () -> Unit,
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
            Column {
                Text(
                    text = stringResource(R.string.dashboard_greeting, uiState.displayName),
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    text = stringResource(R.string.dashboard_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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
                        onCardClick = { viewModel.onWotdClicked(wotd.definition.word) },
                        isRefreshing = uiState.isFetchingWotd,
                        onRefreshClick = { viewModel.onRefreshWotdClicked() }
                    )
                }
            }
        }

        item {
            ScoreBanner(
                score = uiState.userScore,
                onBannerClick = onNavigateToLeaderboard
            )
        }


        item {
            StandardQuizEntryCard(
                onStartClick = onNavigateToQuizHub
            )
            HorizontalDivider(Modifier.padding(vertical = 16.dp))
        }

        item {
            Text(
                text = stringResource(R.string.label_recent_words),
                style = MaterialTheme.typography.titleMedium
            )
            TextButton(onClick = onNavigateToWordHistory) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(R.string.btn_see_all))
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = stringResource(R.string.btn_see_all),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
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
                WordHistoryItem(
                    event = event,
                    onItemClick = { viewModel.onRecentWordClicked(event.word) }
                )
            }
        }
    }

    when (val sheetState = uiState.sheetState) {
        is DashboardSheetState.WordDetail -> {
            WordDetailSheet(
                word = sheetState.word,
                onDismiss = { viewModel.onSheetDismissed() }
            )
        }
        DashboardSheetState.Hidden -> {}
    }
}
