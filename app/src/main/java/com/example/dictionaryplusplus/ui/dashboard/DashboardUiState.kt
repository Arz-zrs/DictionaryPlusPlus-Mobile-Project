package com.example.dictionaryplusplus.ui.dashboard

import com.example.dictionaryplusplus.ui.history.HistoryUiState

data class DashboardUiState(
    val userScore: Int = 0,
    val wordOfTheDay: WotdState = WotdState.Loading,
    val recentWords: List<HistoryUiState> = emptyList(),
    val isQuizAvailable: Boolean = false // TODO: replace in 2E
)
