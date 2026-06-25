package com.example.dictionaryplusplus.data.repository

import com.example.dictionaryplusplus.data.local.UserPreferences
import com.example.dictionaryplusplus.data.local.dao.DefinitionDao
import com.example.dictionaryplusplus.data.local.entity.DefinitionEntity
import com.example.dictionaryplusplus.data.local.mapper.toDomain
import com.example.dictionaryplusplus.domain.model.Definition
import com.example.dictionaryplusplus.domain.repository.WotdRepository
import com.google.gson.Gson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
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
    private val gson: Gson
) : WotdRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeWordOfTheDay(): Flow<Definition?> {
        return userPreferences.wordOfTheDay.flatMapLatest { word ->
            if (word.isBlank()) flowOf(null)
            else definitionDao.observeDefinition(word).map { entity ->
                entity?.toDomain(gson)
            }
        }
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
}