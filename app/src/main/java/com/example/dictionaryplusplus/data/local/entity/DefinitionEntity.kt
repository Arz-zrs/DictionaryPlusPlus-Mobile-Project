package com.example.dictionaryplusplus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "definition_cache")
data class DefinitionEntity(
    @PrimaryKey val word: String,
    val definition: String,
    val phonetic: String?,
    val exampleSentence: String?,
    val relatedWordsJson: String
)
