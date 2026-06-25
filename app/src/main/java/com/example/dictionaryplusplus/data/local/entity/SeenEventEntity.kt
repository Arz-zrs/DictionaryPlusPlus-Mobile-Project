package com.example.dictionaryplusplus.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "seen_event",
    foreignKeys = [
        ForeignKey(
            entity = WordEntity::class,
            parentColumns = ["word"],
            childColumns = ["word"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("word")]
)
data class SeenEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val word: String,
    val seenAtTimestamp: Long,
    val isConfirmed: Boolean = false
)
