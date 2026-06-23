package com.example.dictionaryplusplus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dictionaryplusplus.data.local.entity.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM word_bank WHERE word LIKE :query || '%' ORDER BY word ASC LIMIT 50")
    fun searchWords(query: String): Flow<List<WordEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWords(words: List<WordEntity>)

    @Query("SELECT * FROM word_bank WHERE word NOT IN (SELECT word FROM seen_event) ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomUnseenWord(): WordEntity?

    @Query("SELECT word FROM word_bank WHERE word != :excludedWord ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomDistractors(excludedWord: String, limit: Int): List<String>
}