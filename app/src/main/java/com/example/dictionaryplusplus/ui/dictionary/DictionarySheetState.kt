package com.example.dictionaryplusplus.ui.dictionary

sealed interface DictionarySheetState {
    object Hidden : DictionarySheetState
    data class WordDetail(val word: String) : DictionarySheetState
}