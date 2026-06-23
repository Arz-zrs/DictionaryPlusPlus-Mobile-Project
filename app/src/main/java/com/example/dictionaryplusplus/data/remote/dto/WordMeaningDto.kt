package com.example.dictionaryplusplus.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WordMeaningDto(
    @SerializedName("partOfSpeech") val partOfSpeech: String?, // TODO: remove if unused
    @SerializedName("definitions") val definitions: List<WordDefinitionDto>?,
    @SerializedName("synonyms") val synonyms: List<String>?
)
