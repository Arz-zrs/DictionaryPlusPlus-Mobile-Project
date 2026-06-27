package com.example.dictionaryplusplus.ui.quiz.shared

sealed interface QuizAnswerDisplayState {
    object Unanswered : QuizAnswerDisplayState
    data class Answered(
        val selectedIndex: Int,
        val isCorrect: Boolean,
        val correctIndex: Int,
        val showTimeBonus: Boolean = false
    ): QuizAnswerDisplayState
}