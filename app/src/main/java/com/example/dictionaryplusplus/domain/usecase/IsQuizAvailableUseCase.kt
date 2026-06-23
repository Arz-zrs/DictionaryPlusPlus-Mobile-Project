package com.example.dictionaryplusplus.domain.usecase

import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

class IsQuizAvailableUseCase @Inject constructor() {
    operator fun invoke(
        lastCompletedAt: LocalDateTime?,
        refreshTimeAtLastCompletion: LocalTime,
        now: LocalDateTime
    ): Boolean {
        if (lastCompletedAt == null) return true

        val todayWindowStart = LocalDateTime.of(now.toLocalDate(), refreshTimeAtLastCompletion)
        val currentWindowStart =
            if (now.isBefore(todayWindowStart)) todayWindowStart.minusDays(1)
            else todayWindowStart

        return lastCompletedAt.isBefore(currentWindowStart)
    }
}