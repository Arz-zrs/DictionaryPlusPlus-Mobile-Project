package com.example.dictionaryplusplus.domain.usecase.words

import com.example.dictionaryplusplus.domain.model.PreferenceConstants
import com.example.dictionaryplusplus.domain.repository.WotdRepository
import javax.inject.Inject

class EnsureWotdAvailableUseCase @Inject constructor(
    private val wotdRepository: WotdRepository
) {
    suspend operator fun invoke() {
        val currentWord = wotdRepository.getWordOfTheDay()
        if (currentWord == PreferenceConstants.WOTD_FALLBACK || currentWord.isBlank()) {
            wotdRepository.fetchWotdSync()
        }
    }
}
