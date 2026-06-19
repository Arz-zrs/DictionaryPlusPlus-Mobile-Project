package com.example.dictionaryplusplus.domain.mapper

data class DefinitionCache(
    val word: String,
    val definition: String,
    val phonetic: String?,
    val exampleSentence: String?,
    val synonyms: List<String>
)
