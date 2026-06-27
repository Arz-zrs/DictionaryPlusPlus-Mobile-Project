package com.example.dictionaryplusplus.domain.usecase.quiz

import com.example.dictionaryplusplus.domain.model.LeaderboardUser
import com.example.dictionaryplusplus.domain.repository.LeaderboardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveLeaderboardUseCase @Inject constructor(
    private val leaderboardRepository: LeaderboardRepository
) {
    operator fun invoke(): Flow<List<LeaderboardUser>> = leaderboardRepository.observeLeaderboard()
}