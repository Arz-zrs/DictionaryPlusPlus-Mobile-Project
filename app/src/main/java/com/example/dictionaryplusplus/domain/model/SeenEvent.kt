package com.example.dictionaryplusplus.domain.model

data class SeenEvent(
    val id: Long,
    val word: String,
    val seenAtTimestamp: Long,
    val isConfirmed: Boolean,
    val masteryStatus: MasteryStatus
)
