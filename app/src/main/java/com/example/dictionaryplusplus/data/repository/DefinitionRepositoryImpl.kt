package com.example.dictionaryplusplus.data.repository

import android.content.Context
import com.example.dictionaryplusplus.data.local.dao.DefinitionDao
import com.example.dictionaryplusplus.data.local.entity.DefinitionEntity
import com.example.dictionaryplusplus.data.remote.DictionaryApiService
import com.example.dictionaryplusplus.domain.model.Definition
import com.example.dictionaryplusplus.domain.repository.DefinitionRepository
import com.example.dictionaryplusplus.util.ContentSanitizer
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
            val jsonString = context.assets.open("deny_list.json") // TODO: fill the deny_list.json data
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
            entity?.let {
                Definition(
                    word = it.word,
                    definition = it.definition,
                    phonetic = it.phonetic,
                    exampleSentence = it.exampleSentence,
                    synonyms = gson.fromJson(it.relatedWordsJson, Array<String>::class.java).toList()
                )
            }
        }
    }

    override suspend fun getDefinition(word: String): Result<Definition> {
        return try {
            val cachedDefinition = observeDefinition(word).firstOrNull()
            if (cachedDefinition != null) {
                Result.success(cachedDefinition)
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
                relatedWordsJson = gson.toJson(sanitizedExample),
            )
            definitionDao.insertDefinition(definitionEntity)

            val domainModel = Definition(
                word = word,
                definition = sanitizedDefinition,
                phonetic = phoneticText,
                exampleSentence = sanitizedExample,
                synonyms = sanitizedSynonyms
            )
            Result.success(domainModel)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}