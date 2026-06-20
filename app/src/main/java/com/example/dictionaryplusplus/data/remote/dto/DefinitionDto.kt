package com.example.dictionaryplusplus.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DefinitionDto(
    @SerializedName("definition") val definition: String?,
    @SerializedName("example") val example: String?
)
