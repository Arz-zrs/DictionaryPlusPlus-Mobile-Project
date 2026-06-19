package com.example.dictionaryplusplus.domain.repository

import kotlinx.coroutines.flow.Flow

interface WordNoteRepository {
    fun observeWordNote(word: String): Flow<String>
    suspend fun saveWordNote(word: String, note: String)
}