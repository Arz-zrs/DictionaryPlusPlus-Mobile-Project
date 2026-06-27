package com.example.dictionaryplusplus.ui.quiz.shared

import com.example.dictionaryplusplus.domain.model.QuizQuestion

data class QuestionState(
    val question: QuizQuestion,
    val answerState: AnswerState = AnswerState.Unanswered
)