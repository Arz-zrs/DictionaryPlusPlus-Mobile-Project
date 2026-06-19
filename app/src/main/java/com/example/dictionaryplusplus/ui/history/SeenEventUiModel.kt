package com.example.dictionaryplusplus.ui.history

data class SeenEventUiModel(
    val id: Long,
    val word: String,
    val formattedDate: String,
    val masteryStatus: String
)
