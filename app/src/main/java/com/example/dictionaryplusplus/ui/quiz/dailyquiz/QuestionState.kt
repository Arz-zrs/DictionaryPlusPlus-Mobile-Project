package com.example.dictionaryplusplus.ui.quiz.dailyquiz

import com.example.dictionaryplusplus.domain.model.AnswerScoreResult
import com.example.dictionaryplusplus.domain.model.QuizQuestion

data class QuestionState(
    val question: QuizQuestion,
    val selectedIndex: Int? = null,
    val scoreResult: AnswerScoreResult? = null
)
