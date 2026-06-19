package com.example.dictionaryplusplus.data.repository

import com.example.dictionaryplusplus.data.local.dao.DefinitionCacheDao
import com.example.dictionaryplusplus.domain.mapper.DefinitionCache
import com.example.dictionaryplusplus.domain.repository.DefinitionCacheRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefinitionCacheRepositoryImpl @Inject constructor(
    private val definitionCacheDao: DefinitionCacheDao
) : DefinitionCacheRepository {
    override fun observeDefinition(word: String): Flow<DefinitionCache?> {
        return definitionCacheDao.observeDefinition(word).map { entity ->
            entity?.let {
                DefinitionCache(
                    word = it.word,
                    definition = it.definition,
                    phonetic = it.phonetic,
                    exampleSentence = it.exampleSentence,
                    synonyms = emptyList()
                )
            }
        }
    }
}