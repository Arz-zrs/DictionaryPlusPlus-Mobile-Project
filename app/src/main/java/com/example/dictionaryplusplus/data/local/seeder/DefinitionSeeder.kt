package com.example.dictionaryplusplus.data.local.seeder

import android.content.Context
import com.example.dictionaryplusplus.data.local.UserPreferences
import com.example.dictionaryplusplus.data.local.dao.DefinitionDao
import com.example.dictionaryplusplus.data.local.entity.DefinitionEntity
import com.example.dictionaryplusplus.data.local.seeder.dto.DefinitionSeedDto
import com.example.dictionaryplusplus.core.util.ContentSanitizer
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.getValue

@Singleton
class DefinitionSeeder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val definitionDao: DefinitionDao,
    private val userPreferences: UserPreferences,
    private val sanitizer: ContentSanitizer
) {
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

    suspend fun seedDefinitions() {
        if (userPreferences.isDefinitionSeeded()) return
        try {
            val jsonString = context.assets.open("definition_seed.json")
                .bufferedReader()
                .use { it.readText() }
            val type = object : TypeToken<List<DefinitionSeedDto>>(){}.type
            val seedEntries: List<DefinitionSeedDto> = gson.fromJson(jsonString, type)

            val entities = seedEntries.map {
                val safeDefinition = sanitizer.sanitizeText(it.definition, denyList, "Definition omitted")
                val safeExample = it.exampleSentence?.let { example ->
                    sanitizer.sanitizeText(example, denyList, "Example omitted")
                }
                val safeSynonyms = sanitizer.sanitizeSynonyms(it.synonyms ?: emptyList(), denyList)

                DefinitionEntity(
                    word = it.word,
                    definition = safeDefinition,
                    phonetic = it.phonetic,
                    exampleSentence = safeExample,
                    relatedWordsJson = gson.toJson(safeSynonyms)
                )
            }

            entities.forEach { definitionDao.insertDefinition(it) }
            userPreferences.setDefinitionSeeded(true)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}