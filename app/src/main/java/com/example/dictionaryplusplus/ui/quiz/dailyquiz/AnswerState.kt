package com.example.dictionaryplusplus.ui.quiz.dailyquiz

import com.example.dictionaryplusplus.domain.model.AnswerScoreResult

sealed interface AnswerState {
    data object Unanswered : AnswerState
    data class Answered(
        val selectedIndex: Int,
        val scoreResult: AnswerScoreResult
    ) : AnswerState
}