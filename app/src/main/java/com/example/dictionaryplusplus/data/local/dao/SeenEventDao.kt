package com.example.dictionaryplusplus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dictionaryplusplus.data.local.entity.SeenEventEntity
import com.example.dictionaryplusplus.domain.model.MasteryStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface SeenEventDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSeenEvent(seenEvent: SeenEventEntity): Long

    @Query("SELECT * FROM seen_event ORDER BY seenAtTimestamp DESC")
    fun getAllSeenEvents(): Flow<List<SeenEventEntity>>

    @Query("SELECT * FROM seen_event WHERE masteryStatus = :status ORDER BY seenAtTimestamp DESC")
    fun getSeenEventsByMasteryStatus(status: MasteryStatus): Flow<List<SeenEventEntity>>

    @Query("DELETE FROM seen_event WHERE id = :id")
    suspend fun deleteSeenEventById(id: Long)
}