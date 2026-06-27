package com.example.dictionaryplusplus.data.repository

import com.example.dictionaryplusplus.data.local.UserPreferences
import com.example.dictionaryplusplus.data.local.dao.DefinitionDao
import com.example.dictionaryplusplus.data.local.entity.DefinitionEntity
import com.example.dictionaryplusplus.data.local.mapper.toDomain
import com.example.dictionaryplusplus.core.worker.WotdApiWorker
import com.example.dictionaryplusplus.domain.model.Definition
import com.example.dictionaryplusplus.domain.repository.WotdRepository
import com.google.gson.Gson
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WotdRepositoryImpl @Inject constructor(
    private val userPreferences: UserPreferences,
    private val definitionDao: DefinitionDao,
    private val workManager: WorkManager,
    private val gson: Gson
) : WotdRepository {

    private val _isFetching = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeWordOfTheDay(): Flow<Definition?> {
        return userPreferences.wordOfTheDay.flatMapLatest { word ->
            if (word.isBlank()) flowOf(null)
            else definitionDao.observeDefinition(word).map { entity ->
                entity?.toDomain(gson)
            }
        }
    }

    override fun observeIsFetchingWotd(): Flow<Boolean> = _isFetching.asStateFlow()

    override suspend fun getWordOfTheDay(): String {
        return userPreferences.wordOfTheDay.first()
    }

    override suspend fun setWordOfTheDay(word: String) {
        userPreferences.setWordOfTheDay(word)
    }

    override suspend fun setWordnikWordOfTheDay(word: String, definition: String) {
        val alreadyCached = definitionDao.observeDefinition(word).firstOrNull() != null
        if (!alreadyCached) {
            definitionDao.insertDefinition(
                DefinitionEntity(
                    word = word,
                    definition = definition,
                    phonetic = null,
                    partOfSpeech = null,
                    exampleSentence = null,
                    relatedWordsJson = "[]",
                    meaningsJson = "[]"
                )
            )
        }
        userPreferences.setWordOfTheDay(word)
    }

    override suspend fun fetchWotdSync() {
        _isFetching.value = true
        try {
            val workRequest = OneTimeWorkRequestBuilder<WotdApiWorker>().build()
            workManager.enqueue(workRequest)
            workManager.getWorkInfoByIdFlow(workRequest.id)
                .first { it?.state?.isFinished ?: return@first false }
        } finally {
            _isFetching.value = false
        }
    }
}