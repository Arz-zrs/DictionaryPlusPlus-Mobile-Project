package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.domain.repository.DebugRepository
import javax.inject.Inject

class TriggerWotdWorkerUseCase @Inject constructor(
    private val debugRepository: DebugRepository
) {
    operator fun invoke() {
        debugRepository.triggerWotdWorker()
    }
}
