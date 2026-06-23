package com.example.dictionaryplusplus.ui.quiz

sealed interface QuizAnswerDisplayState {
    object Unanswered : QuizAnswerDisplayState
    data class Answered(
        val selectedIndex: Int,
        val isCorrect: Boolean,
        val correctIndex: Int
    ): QuizAnswerDisplayState
}