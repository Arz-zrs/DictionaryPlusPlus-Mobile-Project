package com.example.dictionaryplusplus.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WordResponseDto(
    @SerializedName("word") val words: String,
    @SerializedName("phonetics") val phonetics: List<PhoneticDto>?,
    @SerializedName("meanings") val meanings: List<MeaningDto>?
)
