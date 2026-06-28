package com.example.dictionaryplusplus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dictionaryplusplus.data.local.entity.WotdHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WotdHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWotdHistory(entry: WotdHistoryEntity)

    @Query("SELECT * FROM wotd_history WHERE date = :date LIMIT 1")
    suspend fun getWotdForDate(date: String): WotdHistoryEntity?

    @Query("SELECT * FROM wotd_history ORDER BY date DESC LIMIT :limit")
    fun observeRecentHistory(limit: Int = 30): Flow<List<WotdHistoryEntity>>

    @Query("DELETE FROM wotd_history")
    suspend fun clearAll()
}