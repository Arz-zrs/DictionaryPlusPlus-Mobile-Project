package com.example.dictionaryplusplus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wotd_history")
data class WotdHistoryEntity(
    @PrimaryKey val date: String,
    val word: String,
    val source: String
)
