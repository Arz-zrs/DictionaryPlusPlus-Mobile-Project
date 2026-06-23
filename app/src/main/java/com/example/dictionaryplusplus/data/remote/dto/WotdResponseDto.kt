package com.example.dictionaryplusplus.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WotdResponseDto(
    @SerializedName("word") val word: String,
    @SerializedName("definitions") val definitions: List<WotdDefinitionDto>?
)
