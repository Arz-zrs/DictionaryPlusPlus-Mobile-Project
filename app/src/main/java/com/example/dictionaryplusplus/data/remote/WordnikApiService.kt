package com.example.dictionaryplusplus.data.remote

import com.example.dictionaryplusplus.data.remote.dto.WotdResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WordnikApiService {
    @GET("words.json/wordOfTheDay")
    suspend fun fetchWordOfTheDay(
        @Query("api_key") apiKey: String
    ): WotdResponseDto
}
