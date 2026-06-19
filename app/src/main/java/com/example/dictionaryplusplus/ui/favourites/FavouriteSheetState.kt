package com.example.dictionaryplusplus.ui.favourites

sealed interface FavouriteSheetState {
    object Hidden : FavouriteSheetState
    data class WordDetail(val word: String) : FavouriteSheetState
}