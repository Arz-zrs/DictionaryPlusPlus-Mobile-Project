package com.example.dictionaryplusplus.data.repository

import com.example.dictionaryplusplus.data.local.dao.WordNoteDao
import com.example.dictionaryplusplus.data.local.entity.WordNoteEntity
import com.example.dictionaryplusplus.domain.repository.WordNoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordNoteRepositoryImpl @Inject constructor(
    private val wordNoteDao: WordNoteDao
) : WordNoteRepository {
    override fun observeWordNote(word: String): Flow<String> {
        return wordNoteDao.observeWordNote(word).map { it?.note ?: "" }
    }

    override suspend fun saveWordNote(word: String, note: String) {
        wordNoteDao.insertWordNote(
            WordNoteEntity(
                word = word,
                note = note,
                lastUpdated = System.currentTimeMillis()
            )
        )
    }
}