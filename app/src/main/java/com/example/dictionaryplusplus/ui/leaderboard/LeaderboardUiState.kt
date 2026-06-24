package com.example.dictionaryplusplus.ui.leaderboard

import com.example.dictionaryplusplus.domain.model.LeaderboardUser

data class LeaderboardUiState(
    val leaderboardList: List<LeaderboardUser> = emptyList(),
    val currentUserRank: Int = 0,
    val totalParticipants: Int = 0,
    val currentUserScore: Int = 0,
    val isLoading: Boolean = false
)
