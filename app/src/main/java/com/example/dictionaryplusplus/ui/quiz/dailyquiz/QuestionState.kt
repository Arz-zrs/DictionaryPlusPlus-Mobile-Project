package com.example.dictionaryplusplus.ui.quiz.dailyquiz

import com.example.dictionaryplusplus.domain.model.QuizQuestion

data class QuestionState(
    val question: QuizQuestion,
    val answerState: AnswerState = AnswerState.Unanswered
)

