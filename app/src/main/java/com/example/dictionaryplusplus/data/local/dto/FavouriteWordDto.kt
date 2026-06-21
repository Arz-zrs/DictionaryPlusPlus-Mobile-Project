package com.example.dictionaryplusplus.data.local.dto

import com.example.dictionaryplusplus.domain.model.MasteryStatus

data class FavouriteWordDto(
    val word: String,
    val definition: String?,
    val masteryStatus: MasteryStatus
)
