package com.example.dictionaryplusplus.ui.dashboard

import com.example.dictionaryplusplus.ui.history.HistoryUiState

data class DashboardUiState(
    val displayName: String = "",
    val userScore: Int = 0,
    val wordOfTheDay: WotdState = WotdState.Loading,
    val recentWords: List<HistoryUiState> = emptyList(),
    val sheetState: DashboardSheetState = DashboardSheetState.Hidden
)

