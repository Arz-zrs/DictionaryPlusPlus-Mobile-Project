package com.example.dictionaryplusplus.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MeaningDto(
    @SerializedName("partOfSpeech") val partOfSpeech: String?,
    @SerializedName("definitions") val definitions: List<DefinitionDto>?,
    @SerializedName("synonyms") val synonyms: List<String>?
)
