package com.example.dictionaryplusplus.domain.repository

import com.example.dictionaryplusplus.domain.mapper.FavouriteWord
import kotlinx.coroutines.flow.Flow

interface FavouriteRepository {
    fun observeIsFavourite(word: String): Flow<Boolean>
    fun observeFavouriteWord(): Flow<List<FavouriteWord>>
    suspend fun toggleFavourite(word: String)
}