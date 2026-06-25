package com.example.dictionaryplusplus.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomBarTab(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    DASHBOARD(Screen.Dashboard.route, "Home", Icons.Default.Home),
    DICTIONARY(Screen.Dictionary.route, "Dictionary", Icons.Default.Search),
    QUIZ(Screen.QuizHub.route, "Quiz", Icons.AutoMirrored.Default.List),
    FAVOURITES(Screen.Favourites.route, "Favourites", Icons.Default.Star),
    SETTINGS(Screen.Settings.route, "Settings", Icons.Default.Settings)
}