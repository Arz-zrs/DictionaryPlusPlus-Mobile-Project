package com.example.dictionaryplusplus.data.repository

import android.content.Context
import com.example.dictionaryplusplus.data.local.dao.DefinitionDao
import com.example.dictionaryplusplus.data.local.entity.DefinitionEntity
import com.example.dictionaryplusplus.data.local.mapper.toDomain
import com.example.dictionaryplusplus.data.remote.DictionaryApiService
import com.example.dictionaryplusplus.domain.model.Definition
import com.example.dictionaryplusplus.domain.model.DefinitionResult
import com.example.dictionaryplusplus.domain.model.DefinitionErrorType
import com.example.dictionaryplusplus.domain.repository.DefinitionRepository
import com.example.dictionaryplusplus.core.util.ContentSanitizer
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefinitionRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val definitionDao: DefinitionDao,
    private val apiService: DictionaryApiService,
    private val sanitizer: ContentSanitizer
) : DefinitionRepository {
    private val gson = Gson()

    private val denyList: Set<String> by lazy {
        try {
            val jsonString = context.assets.open("deny_list.json")
                .bufferedReader()
                .use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            (0 until jsonArray.length()).map { jsonArray.getString(it) }.toSet()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            emptySet()
        }
    }

    override fun observeDefinition(word: String): Flow<Definition?> {
        return definitionDao.observeDefinition(word).map { entity ->
            entity?.toDomain(gson)
        }
    }

    override suspend fun getDefinition(word: String): DefinitionResult {
        return try {
            val cachedDefinition = observeDefinition(word).firstOrNull()
            if (cachedDefinition != null) {
                return DefinitionResult.Success(cachedDefinition)
            }

            val apiResponse = apiService.fetchDefinition(word)
            val firstEntry = apiResponse.firstOrNull() ?: throw Exception("Empty Response")

            val phoneticText = firstEntry.phonetics?.firstOrNull { !it.text.isNullOrEmpty() }?.text
            val firstMeaning = firstEntry.meanings?.firstOrNull()
            val firstDefinition = firstMeaning?.definitions?.firstOrNull()

            val rawDefinition = firstDefinition?.definition ?: "No definition available"
            val rawExample = firstDefinition?.example ?: "No example available"
            val rawSynonyms = firstMeaning?.synonyms ?: emptyList()

            val sanitizedDefinition = sanitizer.sanitizeText(rawDefinition, denyList, "Offensive definition omitted.")
            val sanitizedExample = sanitizer.sanitizeText(rawExample, denyList, "Offensive example omitted.")
            val sanitizedSynonyms = sanitizer.sanitizeSynonyms(rawSynonyms, denyList)

            val definitionEntity = DefinitionEntity(
                word = word,
                definition = sanitizedDefinition,
                phonetic = phoneticText,
                exampleSentence = sanitizedExample,
                relatedWordsJson = gson.toJson(sanitizedSynonyms),
            )
            definitionDao.insertDefinition(definitionEntity)

            val domainModel = Definition(
                word = word,
                definition = sanitizedDefinition,
                phonetic = phoneticText,
                exampleSentence = sanitizedExample,
                synonyms = sanitizedSynonyms
            )
            DefinitionResult.Success(domainModel)
        } catch (e: Exception) {
            val errorType = when (e) {
                is java.net.UnknownHostException -> DefinitionErrorType.NO_INTERNET
                is java.net.SocketTimeoutException -> DefinitionErrorType.TIMEOUT
                is retrofit2.HttpException -> {
                    if (e.code() == 404) DefinitionErrorType.NOT_FOUND else DefinitionErrorType.UNKNOWN
                }
                else -> DefinitionErrorType.UNKNOWN
            }
            DefinitionResult.Error(errorType)
        }
    }
}