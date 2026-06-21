package com.example.dictionaryplusplus.domain.model

data class FavouriteWord(
    val word: String,
    val definition: String?,
    val masteryStatus: MasteryStatus
)
