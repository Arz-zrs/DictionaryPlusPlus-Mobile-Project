package com.example.dictionaryplusplus.ui.dictionary

sealed interface DictionaryUiState {
    object Hidden : DictionaryUiState
    data class WordDetail(val word: String) : DictionaryUiState
}