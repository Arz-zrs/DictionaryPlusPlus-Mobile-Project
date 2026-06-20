package com.example.dictionaryplusplus.data.remote

import com.example.dictionaryplusplus.data.remote.dto.WordResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface DictionaryApiService {
    @GET("{word}")
    suspend fun fetchDefinition(
        @Path("word") word : String
    ): List<WordResponseDto>
}