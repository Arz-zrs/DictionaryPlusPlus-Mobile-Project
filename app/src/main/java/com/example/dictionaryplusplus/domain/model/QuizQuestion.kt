package com.example.dictionaryplusplus.domain.model

data class QuizQuestion(
    val word: String,
    val choices: List<String>,
    val correctAnswerIndex: Int,
    val originalDefinition: String,
    val isFallbackToDefinition: Boolean = false
)
