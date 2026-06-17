package com.example.dictionaryplusplus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "seen_event")
data class SeenEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val word: String,
    val seenAtTimestamp: Long,
    val isConfirmed: Boolean = false,
    val masteryStatus: String = "learning"
)
