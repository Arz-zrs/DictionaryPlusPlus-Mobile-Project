package com.example.dictionaryplusplus.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WordPhoneticDto(
    @SerializedName("text") val text: String?,
    @SerializedName("audio") val audio: String? // TODO: if I don't have time to implement audio playback, remove this
)
