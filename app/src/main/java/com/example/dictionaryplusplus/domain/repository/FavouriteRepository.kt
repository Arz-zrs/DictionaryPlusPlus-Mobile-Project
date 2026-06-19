package com.example.dictionaryplusplus.domain.repository

import kotlinx.coroutines.flow.Flow

interface FavouriteRepository {
    fun observeIsFavourite(word: String): Flow<Boolean>
    suspend fun toggleFavourite(word: String)
}