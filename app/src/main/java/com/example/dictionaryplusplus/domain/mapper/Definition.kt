package com.example.dictionaryplusplus.domain.mapper

data class Definition(
    val word: String,
    val definition: String,
    val phonetic: String?,
    val exampleSentence: String?,
    val synonyms: List<String>
)
