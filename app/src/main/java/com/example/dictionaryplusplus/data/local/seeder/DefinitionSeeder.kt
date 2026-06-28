package com.example.dictionaryplusplus.data.local.seeder

import android.content.Context
import com.example.dictionaryplusplus.data.local.UserPreferences
import com.example.dictionaryplusplus.data.local.dao.DefinitionDao
import com.example.dictionaryplusplus.data.local.entity.DefinitionEntity
import com.example.dictionaryplusplus.data.local.seeder.dto.DefinitionSeedDto
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefinitionSeeder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val definitionDao: DefinitionDao,
    private val userPreferences: UserPreferences
) {
    private val gson = Gson()

    suspend fun seedDefinitions() {
        if (userPreferences.isDefinitionSeeded()) return
        try {
            val jsonString = context.assets.open("definition_seed.json")
                .bufferedReader()
                .use { it.readText() }
            val type = object : TypeToken<List<DefinitionSeedDto>>(){}.type
            val seedEntries: List<DefinitionSeedDto> = gson.fromJson(jsonString, type)

            val entities = seedEntries.map {
                DefinitionEntity(
                    word = it.word,
                    definition = it.definition,
                    phonetic = it.phonetic,
                    exampleSentence = it.exampleSentence,
                    relatedWordsJson = gson.toJson(it.synonyms ?: emptyList<String>())
                )
            }

            entities.forEach { definitionDao.insertDefinition(it) }
            userPreferences.setDefinitionSeeded(true)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}