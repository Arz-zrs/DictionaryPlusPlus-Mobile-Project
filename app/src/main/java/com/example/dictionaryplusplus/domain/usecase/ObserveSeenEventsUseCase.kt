package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.domain.model.HistoryFilter
import com.example.dictionaryplusplus.domain.model.SeenEvent
import com.example.dictionaryplusplus.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSeenEventsUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    operator fun invoke(filter: HistoryFilter): Flow<List<SeenEvent>> {
        return historyRepository.observeSeenEvents(filter)
    }
}
