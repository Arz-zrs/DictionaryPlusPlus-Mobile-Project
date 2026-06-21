package com.example.dictionaryplusplus.domain.model

data class Definition(
    val word: String,
    val definition: String,
    val phonetic: String?,
    val exampleSentence: String?,
    val synonyms: List<String>
)
