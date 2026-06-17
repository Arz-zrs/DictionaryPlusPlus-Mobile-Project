package com.example.dictionaryplusplus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite")
data class FavouriteEntity(
    @PrimaryKey val word: String,
    val addedAtTimestamp: Long
)