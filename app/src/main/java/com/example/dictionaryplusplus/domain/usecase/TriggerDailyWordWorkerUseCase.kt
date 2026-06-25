package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.domain.repository.DebugRepository
import javax.inject.Inject

class TriggerDailyWordWorkerUseCase @Inject constructor(
    private val debugRepository: DebugRepository
) {
    operator fun invoke() {
        debugRepository.triggerDailyWordWorker()
    }
}
