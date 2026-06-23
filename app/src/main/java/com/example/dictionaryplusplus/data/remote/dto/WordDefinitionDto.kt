package com.example.dictionaryplusplus.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WordDefinitionDto(
    @SerializedName("definition") val definition: String?,
    @SerializedName("example") val example: String?
)
