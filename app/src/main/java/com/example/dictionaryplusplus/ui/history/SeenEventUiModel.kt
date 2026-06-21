package com.example.dictionaryplusplus.ui.history

import com.example.dictionaryplusplus.domain.model.MasteryStatus

data class SeenEventUiModel(
    val id: Long,
    val word: String,
    val formattedDate: String,
    val masteryStatus: MasteryStatus
)
