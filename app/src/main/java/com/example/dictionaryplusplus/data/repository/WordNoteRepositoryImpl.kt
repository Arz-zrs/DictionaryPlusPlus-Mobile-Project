package com.example.dictionaryplusplus.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.example.dictionaryplusplus.data.local.dao.WordNoteDao
import com.example.dictionaryplusplus.data.local.entity.WordNoteEntity
import com.example.dictionaryplusplus.domain.repository.WordNoteRepository
import com.google.firebase.crashlytics.FirebaseCrashlytics
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
        try {
            wordNoteDao.insertWordNote(
                WordNoteEntity(
                    word = word,
                    note = note,
                    lastUpdated = System.currentTimeMillis()
                )
            )
        } catch (e: SQLiteConstraintException) {
            FirebaseCrashlytics.getInstance().recordException(
                Exception("Foreign Key Violation: can't toggle favourite on non-existent word: $word", e)
            )
        }
    }
}