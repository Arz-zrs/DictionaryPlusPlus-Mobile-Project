package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.domain.repository.WotdRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveIsFetchingWotdUseCase @Inject constructor(
    private val wotdRepository: WotdRepository
) {
    operator fun invoke(): Flow<Boolean> = wotdRepository.observeIsFetchingWotd()
}
