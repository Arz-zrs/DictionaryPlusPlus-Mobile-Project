package com.example.dictionaryplusplus.domain.repository

import com.example.dictionaryplusplus.domain.model.Word
import kotlinx.coroutines.flow.Flow

interface WordRepository {
    fun searchWords(query: String): Flow<List<Word>>
    suspend fun getRandomDistractors(excludedWord: String, limit: Int): List<String>
    suspend fun getRandomSeenWord(): String?
    suspend fun getRandomWords(limit: Int): List<String>
}
