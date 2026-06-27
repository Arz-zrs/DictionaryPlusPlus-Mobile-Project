package com.example.dictionaryplusplus.domain.usecase.words

import com.example.dictionaryplusplus.domain.repository.HistoryRepository
import javax.inject.Inject

class SetSeenEventUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(word: String) {
        historyRepository.addSeenEvent(word)
    }
}