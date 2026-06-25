package com.example.dictionaryplusplus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word_bank")
data class WordEntity(
    @PrimaryKey val word: String
)
