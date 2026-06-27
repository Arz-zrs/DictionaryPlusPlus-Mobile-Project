package com.example.dictionaryplusplus.ui.quiz.shared

data class QuestionDisplayData(
    val title: String,
    val prompt: String,
    val choices: List<String>,
    val answerState: QuizAnswerDisplayState
)