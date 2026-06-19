package com.example.dictionaryplusplus.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.dictionaryplusplus.data.local.entity.SeenEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SeenEventDao {
    @Query("SELECT * FROM seen_event ORDER BY seenAtTimestamp DESC")
    fun getAllSeenEvents(): Flow<List<SeenEventEntity>>

    @Query("SELECT * FROM seen_event WHERE masteryStatus = :status ORDER BY seenAtTimestamp DESC")
    fun getSeenEventsByMasteryStatus(status: String): Flow<List<SeenEventEntity>>

    @Query("DELETE FROM seen_event WHERE id = :id")
    suspend fun deleteSeenEventById(id: Long)
}