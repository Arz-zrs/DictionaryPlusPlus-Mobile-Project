package com.example.dictionaryplusplus.core.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object WordHistory : Screen("word_history")
    object Dictionary : Screen("dictionary")
    object QuizHub : Screen("quiz_hub")

    object SynonymQuiz : Screen("synonym_quiz?word={word}") {
        fun createRoute(word: String?) =
            if (word != null) "synonym_quiz?word=$word"
            else "synonym_quiz"
    }

    object DailyQuiz : Screen("daily_quiz")
    object Leaderboard : Screen("leaderboard")
    object Favourites : Screen("favourites")
    object Settings : Screen("settings")
}