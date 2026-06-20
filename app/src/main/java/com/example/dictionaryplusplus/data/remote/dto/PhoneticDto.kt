package com.example.dictionaryplusplus.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PhoneticDto(
    @SerializedName("text") val text: String?,
    @SerializedName("audio") val audio: String?
)
