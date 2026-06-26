package com.example.dictionaryplusplus.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WordPhoneticDto(
    @SerializedName("text") val text: String?
)
