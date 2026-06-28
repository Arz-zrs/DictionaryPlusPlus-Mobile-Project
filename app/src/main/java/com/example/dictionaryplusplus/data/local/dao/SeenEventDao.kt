package com.example.dictionaryplusplus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dictionaryplusplus.data.local.entity.SeenEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SeenEventDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSeenEvent(seenEvent: SeenEventEntity): Long

    @Query("SELECT * FROM seen_event ORDER BY seenAtTimestamp DESC")
    fun getAllSeenEvents(): Flow<List<SeenEventEntity>>

    @Query("DELETE FROM seen_event WHERE id = :id")
    suspend fun deleteSeenEventById(id: Long)

    @Query("UPDATE seen_event SET isConfirmed = 1 WHERE id = :id")
    suspend fun confirmSeenEvent(id: Long)

    @Query("""
        SELECT s.word FROM seen_event s
        INNER JOIN definition_cache d ON s.word = d.word
        ORDER BY RANDOM()
        LIMIT 1
    """)
    suspend fun getRandomSeenWord(): String?

    @Query("DELETE FROM seen_event")
    suspend fun clearAll()
}
