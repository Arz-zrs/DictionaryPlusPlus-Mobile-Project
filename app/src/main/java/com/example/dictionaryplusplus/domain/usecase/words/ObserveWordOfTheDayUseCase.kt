package com.example.dictionaryplusplus.domain.usecase.words

import com.example.dictionaryplusplus.domain.model.Definition
import com.example.dictionaryplusplus.domain.repository.WotdRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveWordOfTheDayUseCase @Inject constructor(
    private val wotdRepository: WotdRepository
) {
    operator fun invoke(): Flow<Definition?> {
        return wotdRepository.observeWordOfTheDay()
    }
}
