package com.example.dictionaryplusplus.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.dictionaryplusplus.domain.model.MasteryStatus

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
    indices = [Index("word"), Index("masteryStatus")]
)
data class SeenEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val word: String,
    val seenAtTimestamp: Long,
    val isConfirmed: Boolean = false,
    val masteryStatus: MasteryStatus = MasteryStatus.LEARNING
)
