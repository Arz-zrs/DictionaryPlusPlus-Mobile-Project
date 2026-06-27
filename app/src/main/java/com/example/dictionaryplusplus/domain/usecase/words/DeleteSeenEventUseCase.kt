package com.example.dictionaryplusplus.domain.usecase.words

import com.example.dictionaryplusplus.domain.repository.HistoryRepository
import javax.inject.Inject

class DeleteSeenEventUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(id: Long) {
        historyRepository.deleteSeenEvent(id)
    }
}
