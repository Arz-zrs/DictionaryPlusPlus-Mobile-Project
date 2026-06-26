package com.example.dictionaryplusplus.ui.quiz.practicequiz

sealed interface QuizAnswerState {
    data object Unanswered : QuizAnswerState
    data class Answered(val selectedIndex: Int, val isCorrect: Boolean) : QuizAnswerState
}