package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.domain.repository.LeaderboardRepository
import javax.inject.Inject

class GetUserRankUseCase @Inject constructor(
    private val leaderboardRepository: LeaderboardRepository
) {
    suspend operator fun invoke(score: Int): Result<Int> = leaderboardRepository.getUserRank(score)
}