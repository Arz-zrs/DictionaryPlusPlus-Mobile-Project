package com.example.dictionaryplusplus.ui.history

sealed interface HistorySheetState {
    object Hidden : HistorySheetState
    data class WordDetail(val word: String) : HistorySheetState
}