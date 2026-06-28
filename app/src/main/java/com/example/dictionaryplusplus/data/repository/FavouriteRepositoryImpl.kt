package com.example.dictionaryplusplus.data.repository

import com.example.dictionaryplusplus.core.di.ApplicationScope
import com.example.dictionaryplusplus.data.firebase.FirestoreSyncStore
import com.example.dictionaryplusplus.data.local.dao.FavouriteDao
import com.example.dictionaryplusplus.data.local.entity.FavouriteEntity
import com.example.dictionaryplusplus.domain.model.FavouriteWord
import com.example.dictionaryplusplus.domain.repository.FavouriteRepository
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavouriteRepositoryImpl @Inject constructor(
    private val favouriteDao: FavouriteDao,
    private val firestoreSyncStore: FirestoreSyncStore,
    @ApplicationScope private val applicationScope: CoroutineScope
) : FavouriteRepository {
    override fun observeIsFavourite(word: String): Flow<Boolean> {
        return favouriteDao.observeIsFavourite(word)
    }

    override suspend fun toggleFavourite(word: String): Boolean {
        return try {
            val isFavourite =
                favouriteDao.observeIsFavourite(word).map { it }.firstOrNull() ?: false

            val newState = !isFavourite
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
            applicationScope.launch(Dispatchers.IO) {
                firestoreSyncStore.syncFavouriteChange(word, isAdded = newState)
            }
            newState
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            false
        }
    }

    override fun observeFavouriteWord(): Flow<List<FavouriteWord>> {
        return favouriteDao.observeFavouriteWords().map { list ->
            list.map { dto ->
                FavouriteWord(
                    word = dto.word,
                    definition = dto.definition
                )
            }
        }
    }
}