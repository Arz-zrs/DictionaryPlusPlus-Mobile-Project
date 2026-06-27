package com.example.dictionaryplusplus.domain.repository

import com.example.dictionaryplusplus.domain.model.Definition
import kotlinx.coroutines.flow.Flow

interface WotdRepository {

    fun observeWordOfTheDay(): Flow<Definition?>
    fun observeIsFetchingWotd(): Flow<Boolean>
    suspend fun getWordOfTheDay(): String
    suspend fun setWordOfTheDay(word: String)
    suspend fun setWordnikWordOfTheDay(word: String, definition: String)
    suspend fun fetchWotdSync()
}