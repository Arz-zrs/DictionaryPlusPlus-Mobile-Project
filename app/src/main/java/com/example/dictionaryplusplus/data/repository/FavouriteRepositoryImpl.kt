package com.example.dictionaryplusplus.data.repository

import com.example.dictionaryplusplus.data.local.dao.FavouriteDao
import com.example.dictionaryplusplus.data.local.entity.FavouriteEntity
import com.example.dictionaryplusplus.domain.repository.FavouriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavouriteRepositoryImpl @Inject constructor(
    private val favouriteDao: FavouriteDao
) : FavouriteRepository {
    override fun observeIsFavourite(word: String): Flow<Boolean> {
        return favouriteDao.observeIsFavourite(word)
    }

    override suspend fun toggleFavourite(word: String) {
        val isFavourite =
            favouriteDao.observeIsFavourite(word).map { it }.firstOrNull() ?: false

        if (isFavourite) {
            favouriteDao.deleteFavourite(word)
        } else {
            favouriteDao.insertFavourite(
                FavouriteEntity(
                    word = word,
                    addedAtTimestamp = System.currentTimeMillis()
                )
            )
        }
    }
}