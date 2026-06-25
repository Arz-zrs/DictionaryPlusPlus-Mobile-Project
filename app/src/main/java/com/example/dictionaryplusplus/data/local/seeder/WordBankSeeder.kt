package com.example.dictionaryplusplus.data.local.seeder

import android.content.Context
import com.example.dictionaryplusplus.data.local.UserPreferences
import com.example.dictionaryplusplus.data.local.dao.WordDao
import com.example.dictionaryplusplus.data.local.entity.WordEntity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordBankSeeder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val wordDao: WordDao,
    private val userPreferences: UserPreferences
) {
    suspend fun seedWordBank() {
        if (userPreferences.isWordBankSeeded()) return

        try {
            val jsonString = context.assets.open("word_bank.json")
                .bufferedReader()
                .use { it.readText() }

            val words = JSONArray(jsonString)
                .let { array -> (0 until array.length()).map { array.getString(it) } }
                .map { word ->
                    WordEntity(
                        word = word
                    )
                }

            wordDao.insertWords(words)
            userPreferences.setWordBankSeeded(true)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}