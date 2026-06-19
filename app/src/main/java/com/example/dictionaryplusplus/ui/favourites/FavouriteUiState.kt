package com.example.dictionaryplusplus.ui.favourites

sealed interface FavouriteUiState {
    object Hidden : FavouriteUiState
    data class WordDetail(val word: String) : FavouriteUiState
}