package com.example.dictionaryplusplus.data.repository

import android.content.Context
import com.example.dictionaryplusplus.data.local.dao.DefinitionDao
import com.example.dictionaryplusplus.data.local.entity.DefinitionEntity
import com.example.dictionaryplusplus.data.remote.DictionaryApiService
import com.example.dictionaryplusplus.domain.mapper.Definition
import com.example.dictionaryplusplus.domain.repository.DefinitionRepository
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
    private val apiService: DictionaryApiService
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
            val cachedDefinition = observeDefinition(word).map { it }.firstOrNull()
            if (cachedDefinition != null) {
                Result.success(cachedDefinition)
            } else {
                Result.failure(Exception("Definition not found in cache"))
            }

            val apiResponse = apiService.fetchDefinition(word)
            val firstEntry = apiResponse.firstOrNull() ?: throw Exception("No definition found")

            val phoneticText = firstEntry.phonetics?.firstOrNull { !it.text.isNullOrEmpty() }?.text
            val firstMeaning = firstEntry.meanings?.firstOrNull()
            val firstDefinition = firstMeaning?.definitions?.firstOrNull()

            val rawDefinition = firstDefinition?.definition ?: "No definition available"
            val rawExample = firstDefinition?.example ?: "No example available"
            val rawSynonyms = firstMeaning?.synonyms ?: emptyList()

            val sanitizedDefinition = sanitizeText(rawDefinition, "Offensive definition omitted.")
            val sanitizedExample = sanitizeText(rawExample, "Offensive example omitted.")
            val sanitizedSynonyms = sanitizeSynonyms(rawSynonyms)

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

    private fun sanitizeText(text: String, fallback: String): String {
        val words = text.lowercase().split(Regex("[^a-zA-Z0-9']+"))
        val containsSensitiveWords = words.any { denyList.contains(it) }
        return if (containsSensitiveWords) fallback else text
    }

    private fun sanitizeSynonyms(synonyms: List<String>): List<String> {
        return synonyms.filter { !denyList.contains(it.lowercase()) }
    }
}