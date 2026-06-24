package com.example.dictionaryplusplus.domain.repository

import com.example.dictionaryplusplus.domain.model.LeaderboardUser
import kotlinx.coroutines.flow.Flow

interface LeaderboardRepository {
    fun observeLeaderboard(): Flow<List<LeaderboardUser>>
    suspend fun getTotalParticipantCount(): Result<Long>
    suspend fun getUserRank(score: Int): Result<Int>
}