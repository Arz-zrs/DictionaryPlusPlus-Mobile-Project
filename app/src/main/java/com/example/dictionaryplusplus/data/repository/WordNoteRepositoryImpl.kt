package com.example.dictionaryplusplus.data.repository

import com.example.dictionaryplusplus.core.di.ApplicationScope
import com.example.dictionaryplusplus.data.firebase.FirestoreSyncStore
import com.example.dictionaryplusplus.data.local.dao.WordNoteDao
import com.example.dictionaryplusplus.data.local.entity.WordNoteEntity
import com.example.dictionaryplusplus.domain.repository.WordNoteRepository
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordNoteRepositoryImpl @Inject constructor(
    private val wordNoteDao: WordNoteDao,
    private val fireStoreSyncStore: FirestoreSyncStore,
    @ApplicationScope private val applicationScope: CoroutineScope
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
            applicationScope.launch(Dispatchers.IO) {
                fireStoreSyncStore.syncNoteChange(word, note)
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}