package com.example.dictionaryplusplus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dictionaryplusplus.data.local.entity.WordNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordNoteDao {
    @Query("SELECT * FROM word_note WHERE word = :word LIMIT 1")
    fun observeWordNote(word: String): Flow<WordNoteEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordNote(wordNote: WordNoteEntity)

    @Query("DELETE FROM word_note")
    suspend fun clearAll()
}