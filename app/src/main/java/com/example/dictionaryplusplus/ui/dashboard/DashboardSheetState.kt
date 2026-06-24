package com.example.dictionaryplusplus.ui.dashboard

sealed interface DashboardSheetState {
    data object Hidden : DashboardSheetState
    data class WordDetail(val word: String) : DashboardSheetState
}