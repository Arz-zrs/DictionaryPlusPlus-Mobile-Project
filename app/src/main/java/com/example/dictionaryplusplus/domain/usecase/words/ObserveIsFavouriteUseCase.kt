package com.example.dictionaryplusplus.domain.usecase.words

import com.example.dictionaryplusplus.domain.repository.FavouriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveIsFavouriteUseCase @Inject constructor(
    private val favouriteRepository: FavouriteRepository
) {
    operator fun invoke(word: String): Flow<Boolean> {
        return favouriteRepository.observeIsFavourite(word)
    }
}
