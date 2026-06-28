package com.example.dictionaryplusplus.domain.usecase.words

import com.example.dictionaryplusplus.domain.repository.WotdRepository
import java.time.LocalDate
import javax.inject.Inject

class EnsureWotdAvailableUseCase @Inject constructor(
    private val wotdRepository: WotdRepository
) {
    suspend operator fun invoke() {
        val today = LocalDate.now().toString()
        val todayEntry = wotdRepository.getWotdHistoryForDate(today)

        if (todayEntry == null) {
            wotdRepository.fetchWotdSync()
        }
    }
}
