package com.example.dictionaryplusplus.domain.model

data class WotdHistoryEntry(
    val date: String,
    val word: String,
    val source: WotdSource
)
