package com.example.dictionaryplusplus.ui.history

interface HistorySheetState {
    object Hidden : HistorySheetState
    data class WordDetail(val word: String) : HistorySheetState
}