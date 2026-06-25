package com.example.dictionaryplusplus.domain.model

data class Definition(
    val word: String,
    val definition: String,
    val phonetic: String?,
    val partOfSpeech: String? = null,
    val exampleSentence: String?,
    val synonyms: List<String>,
    val meanings: List<WordMeaning> = emptyList()
)
