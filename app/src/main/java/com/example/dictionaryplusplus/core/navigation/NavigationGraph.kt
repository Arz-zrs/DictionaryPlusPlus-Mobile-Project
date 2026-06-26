package com.example.dictionaryplusplus.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.dictionaryplusplus.ui.auth.LoginScreen
import com.example.dictionaryplusplus.ui.auth.RegisterScreen
import com.example.dictionaryplusplus.ui.dashboard.DashboardScreen
import com.example.dictionaryplusplus.ui.dictionary.DictionaryScreen
import com.example.dictionaryplusplus.ui.favourites.FavouritesScreen
import com.example.dictionaryplusplus.ui.history.WordHistoryScreen
import com.example.dictionaryplusplus.ui.leaderboard.LeaderboardScreen
import com.example.dictionaryplusplus.ui.onboarding.OnboardingScreen
import com.example.dictionaryplusplus.ui.quiz.dailyquiz.DailyQuizScreen
import com.example.dictionaryplusplus.ui.quiz.QuizScreen
import com.example.dictionaryplusplus.ui.quiz.practicequiz.PracticeQuizScreen
import com.example.dictionaryplusplus.ui.settings.SettingsScreen

@Composable
fun NavigationGraph(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = Screen.Onboarding.route) {
            OnboardingScreen(onNavigateToLogin = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            })
        }

        composable(route = Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(route = Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        composable(route = Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToQuizHub = {
                    navController.navigate(Screen.DailyQuiz.route)
                },
                onNavigateToLeaderboard = {
                    navController.navigate(Screen.Leaderboard.route)
                },
                onNavigateToWordHistory = {
                    navController.navigate(Screen.WordHistory.route)
                }
            )
        }

        composable(route = Screen.WordHistory.route) {
            WordHistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.Dictionary.route) {
            DictionaryScreen()
        }

        composable(route = Screen.QuizHub.route) {
            QuizScreen(
                onNavigateToPracticeQuiz = {
                    navController.navigate(Screen.PracticeQuiz.createRoute(null))
                },
                onNavigateToDailyQuiz = {
                    navController.navigate(Screen.DailyQuiz.route)
                }
            )
        }

        composable(
            route = Screen.PracticeQuiz.route,
            arguments = listOf(
                navArgument("word") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = "dictionaryplusplus://quiz/{word}" }
            )
        ) { _ ->
            PracticeQuizScreen(
                onNavigateBack = {
                    if (!navController.popBackStack()) {
                        navController.navigate(Screen.Dashboard.route)
                    }
                }
            )
        }

        composable(route = Screen.DailyQuiz.route) {
            DailyQuizScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.Leaderboard.route) {
            LeaderboardScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.Favourites.route) {
            FavouritesScreen()
        }

        composable(route = Screen.Settings.route) {
            SettingsScreen(
                onLogoutSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
