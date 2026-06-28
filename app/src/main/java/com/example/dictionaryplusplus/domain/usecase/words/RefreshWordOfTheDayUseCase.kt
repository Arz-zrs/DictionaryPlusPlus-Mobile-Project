package com.example.dictionaryplusplus.domain.usecase.words

import com.example.dictionaryplusplus.domain.repository.WotdRepository
import javax.inject.Inject

class RefreshWordOfTheDayUseCase @Inject constructor(
    private val wotdRepository: WotdRepository
) {
    suspend operator fun invoke() = wotdRepository.fetchWotdSync()
}