package com.example.dictionaryplusplus.data.repository

import com.example.dictionaryplusplus.data.local.dao.DefinitionDao
import com.example.dictionaryplusplus.data.local.entity.DefinitionEntity
import com.example.dictionaryplusplus.data.local.mapper.toDomain
import com.example.dictionaryplusplus.data.remote.DictionaryApiService
import com.example.dictionaryplusplus.domain.model.Definition
import com.example.dictionaryplusplus.domain.model.DefinitionResult
import com.example.dictionaryplusplus.domain.model.DefinitionErrorType
import com.example.dictionaryplusplus.domain.repository.DefinitionRepository
import com.example.dictionaryplusplus.core.util.ContentSanitizer
import com.example.dictionaryplusplus.core.util.DenyListProvider
import com.example.dictionaryplusplus.data.local.dao.WordDao
import com.example.dictionaryplusplus.data.local.entity.WordEntity
import com.example.dictionaryplusplus.domain.model.WordMeaning
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefinitionRepositoryImpl @Inject constructor(
    private val definitionDao: DefinitionDao,
    private val apiService: DictionaryApiService,
    private val wordDao: WordDao,
    private val sanitizer: ContentSanitizer,
    private val denyListProvider: DenyListProvider
) : DefinitionRepository {
    private val gson = Gson()

    override fun observeDefinition(word: String): Flow<Definition?> {
        return definitionDao.observeDefinition(word).map { entity ->
            entity?.toDomain(gson)
        }
    }

    override suspend fun getDefinitionOnce(word: String): Definition? {
        return definitionDao.getDefinition(word)?.toDomain(gson)
    }

    override suspend fun getDefinition(word: String): DefinitionResult {
        return try {
            val cachedDefinition = getDefinitionOnce(word)
            if (cachedDefinition != null) {
                return DefinitionResult.Success(cachedDefinition)
            }

            val apiResponse = apiService.fetchDefinition(word)
            val firstEntry = apiResponse.firstOrNull() ?: throw Exception("Empty Response")

            val phoneticText = firstEntry.phonetics?.firstOrNull { !it.text.isNullOrEmpty() }?.text

            val rawMeanings = firstEntry.meanings
                ?.take(3)
                ?.mapNotNull { meaning ->
                    val pos = meaning.partOfSpeech ?: return@mapNotNull null
                    val def = meaning.definitions?.firstOrNull()?.definition ?: return@mapNotNull null
                    Triple(pos, def, meaning.definitions.firstOrNull()?.example)
                } ?: emptyList()

            val rawDefinition = rawMeanings.firstOrNull()?.second ?: "No definition available"
            val rawExample = rawMeanings.firstOrNull()?.third ?: "No example available"
            val rawSynonyms = firstEntry.meanings?.firstOrNull()?.synonyms ?: emptyList()

            val denyList = denyListProvider.denyList
            val sanitizedDefinition = sanitizer.sanitizeText(rawDefinition, denyList,
                ContentSanitizer.FALLBACK_DEFINITION)
            val sanitizedExample = sanitizer.sanitizeText(rawExample, denyList, ContentSanitizer.FALLBACK_EXAMPLE)
            val sanitizedSynonyms = sanitizer.sanitizeSynonyms(rawSynonyms, denyList)
            val sanitizedMeanings = rawMeanings.map { (pos, def, ex) ->
                Triple(pos, sanitizer.sanitizeText(def, denyList, ContentSanitizer.FALLBACK_DEFINITION),
                    ex?.let { sanitizer.sanitizeText(it, denyList, ContentSanitizer.FALLBACK_EXAMPLE) })
            }.filter { !ContentSanitizer.isFallbackDefinition(it.second) }

            val definitionEntity = DefinitionEntity(
                word = word,
                definition = sanitizedDefinition,
                phonetic = phoneticText,
                partOfSpeech = rawMeanings.firstOrNull()?.first,
                exampleSentence = sanitizedExample,
                relatedWordsJson = gson.toJson(sanitizedSynonyms),
                meaningsJson = gson.toJson(sanitizedMeanings)
            )
            definitionDao.insertDefinition(definitionEntity)

            try {
                wordDao.insertWords(listOf(WordEntity(word)))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }

            val domainModel = Definition(
                word = word,
                definition = sanitizedDefinition,
                phonetic = phoneticText,
                partOfSpeech = rawMeanings.firstOrNull()?.first,
                exampleSentence = sanitizedExample,
                synonyms = sanitizedSynonyms,
                meanings = sanitizedMeanings.map { (pos, def, ex) ->
                    WordMeaning(pos, def, ex)
                }
            )
            DefinitionResult.Success(domainModel)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            val errorType = when (e) {
                is java.net.UnknownHostException -> DefinitionErrorType.NO_INTERNET
                is java.net.SocketTimeoutException -> DefinitionErrorType.TIMEOUT
                is retrofit2.HttpException -> {
                    if (e.code() == 404) DefinitionErrorType.NOT_FOUND
                    else DefinitionErrorType.UNKNOWN
                }
                else -> DefinitionErrorType.UNKNOWN
            }
            DefinitionResult.Error(errorType)
        }
    }
}