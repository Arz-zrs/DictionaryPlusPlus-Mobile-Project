package com.example.dictionaryplusplus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dictionaryplusplus.data.local.dto.FavouriteWordDto
import com.example.dictionaryplusplus.data.local.entity.FavouriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteDao {
    @Query("SELECT EXISTS(SELECT 1 FROM favourite WHERE word = :word LIMIT 1)")
    fun observeIsFavourite(word: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavourite(favourite: FavouriteEntity)

    @Query("DELETE FROM favourite WHERE word = :word")
    suspend fun deleteFavourite(word: String)

    @Query("""
        SELECT f.word, d.definition
        FROM favourite f
        LEFT JOIN definition_cache d ON f.word = d.word
        ORDER BY f.addedAtTimestamp DESC
    """)
    fun observeFavouriteWords(): Flow<List<FavouriteWordDto>>

    @Query("DELETE FROM favourite")
    suspend fun clearAll()
}