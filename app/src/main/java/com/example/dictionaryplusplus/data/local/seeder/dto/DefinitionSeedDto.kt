package com.example.dictionaryplusplus.data.local.seeder.dto

import com.google.gson.annotations.SerializedName

data class DefinitionSeedDto(
    @SerializedName("word") val word: String,
    @SerializedName("definition") val definition: String,
    @SerializedName("phonetic") val phonetic: String,
    @SerializedName("exampleSentence") val exampleSentence: String?,
    @SerializedName("synonyms") val synonyms: List<String>?
)
