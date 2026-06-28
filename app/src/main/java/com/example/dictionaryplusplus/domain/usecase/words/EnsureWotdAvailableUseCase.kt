package com.example.dictionaryplusplus.domain.usecase.words

import com.example.dictionaryplusplus.domain.repository.WotdRepository
import kotlinx.coroutines.withTimeoutOrNull
import java.time.LocalDate
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class EnsureWotdAvailableUseCase @Inject constructor(
    private val wotdRepository: WotdRepository
) {
    suspend operator fun invoke() {
        val today = LocalDate.now().toString()
        val todaysEntry = wotdRepository.getWotdHistoryForDate(today)

        if (todaysEntry == null) {
            val fetched = withTimeoutOrNull(5000.milliseconds) {
                wotdRepository.fetchWotdSync()
                true
            }
            if (fetched == null) {
                wotdRepository.fallbackToLocalWotd()
            }
        }
    }
}
