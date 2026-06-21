package com.example.dictionaryplusplus.domain.repository

import com.example.dictionaryplusplus.domain.model.Word
import kotlinx.coroutines.flow.Flow

interface WordRepository {
    fun searchWords(query: String): Flow<List<Word>>
}