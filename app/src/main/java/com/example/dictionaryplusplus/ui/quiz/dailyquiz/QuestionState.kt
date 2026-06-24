package com.example.dictionaryplusplus.ui.quiz.dailyquiz

import com.example.dictionaryplusplus.domain.model.AnswerScoreResult
import com.example.dictionaryplusplus.domain.model.QuizQuestion

data class QuestionState(
    val question: QuizQuestion,
    val answerState: AnswerState = AnswerState.Unanswered
)

sealed interface AnswerState {
    data object Unanswered : AnswerState
    data class Answered(
        val selectedIndex: Int,
        val scoreResult: AnswerScoreResult
    ) : AnswerState
}
