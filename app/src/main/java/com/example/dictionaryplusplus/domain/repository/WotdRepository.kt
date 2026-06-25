package com.example.dictionaryplusplus.domain.repository

import com.example.dictionaryplusplus.domain.model.Definition
import kotlinx.coroutines.flow.Flow

interface WotdRepository {

    fun observeWordOfTheDay(): Flow<Definition?>
    suspend fun setWordOfTheDay(word: String)
    suspend fun setWordnikWordOfTheDay(word: String, definition: String)
}