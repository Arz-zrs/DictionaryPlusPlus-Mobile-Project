package com.example.dictionaryplusplus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word_note")
data class WordNoteEntity(
    @PrimaryKey val word: String,
    val note: String,
    val lastUpdated: Long
)