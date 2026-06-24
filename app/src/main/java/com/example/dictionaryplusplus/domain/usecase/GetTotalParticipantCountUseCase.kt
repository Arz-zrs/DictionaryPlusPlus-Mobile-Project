package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.domain.repository.LeaderboardRepository
import javax.inject.Inject

class GetTotalParticipantCountUseCase @Inject constructor(
    private val leaderboardRepository: LeaderboardRepository
) {
    suspend operator fun invoke(): Result<Long> = leaderboardRepository.getTotalParticipantCount()
}