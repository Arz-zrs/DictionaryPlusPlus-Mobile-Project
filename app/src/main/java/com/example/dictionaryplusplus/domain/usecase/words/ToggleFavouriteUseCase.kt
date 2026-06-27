package com.example.dictionaryplusplus.domain.usecase.words

import com.example.dictionaryplusplus.domain.repository.FavouriteRepository
import javax.inject.Inject

class ToggleFavouriteUseCase @Inject constructor(
    private val favouriteRepository: FavouriteRepository
) {
    suspend operator fun invoke(word: String): Boolean {
        return favouriteRepository.toggleFavourite(word)
    }
}
