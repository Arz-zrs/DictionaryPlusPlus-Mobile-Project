package com.example.dictionaryplusplus.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("user_preferences")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
        val NOTIFICATION_TIME = stringPreferencesKey("notification_time")
        val IS_WORD_BANK_SEEDED = booleanPreferencesKey("is_word_bank_seeded")
        val IS_DEFINITION_SEEDED = booleanPreferencesKey("is_definition_seeded")
        val WORD_OF_THE_DAY = stringPreferencesKey("word_of_the_day")
        val QUIZ_LENGTH = intPreferencesKey("quiz_length")
        val DAILY_QUIZ_REFRESH_TIME = stringPreferencesKey("daily_quiz_refresh_time")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val FONT_SIZE = stringPreferencesKey("font_size")
        val LAST_COMPLETED_AT_TIMESTAMP = longPreferencesKey("last_completed_at_timestamp")
        val REFRESH_TIME_AT_LAST_COMPLETION = stringPreferencesKey("refresh_time_at_last_completion")
    }

    companion object {
        const val WOTD_FALLBACK: String = "river"
        const val DEFAULT_QUIZ_LENGTH: Int = 5
        const val DEFAULT_REFRESH_TIMESTAMP: String = "06:00"
        const val DEFAULT_NOTIF_TIMESTAMP: String = "08:00"
        const val DEFAULT_THEME = "SYSTEM"
        const val DEFAULT_FONT_SIZE = "MEDIUM"
    }

    val hasSeenOnboarding: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.HAS_SEEN_ONBOARDING] ?: false
        }

    suspend fun setHasSeenOnboarding(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAS_SEEN_ONBOARDING] = completed
        }
    }

    val notificationTime: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.NOTIFICATION_TIME] ?: DEFAULT_NOTIF_TIMESTAMP
        }

    suspend fun setNotificationTime(time: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATION_TIME] = time
        }
    }

    suspend fun isWordBankSeeded(): Boolean {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[PreferencesKeys.IS_WORD_BANK_SEEDED] ?: false
            }
            .first()
    }

    suspend fun setWordBankSeeded(seeded: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_WORD_BANK_SEEDED] = seeded
        }
    }

    suspend fun isDefinitionSeeded(): Boolean {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[PreferencesKeys.IS_DEFINITION_SEEDED] ?: false
            }
            .first()
    }

    suspend fun setDefinitionSeeded(seeded: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DEFINITION_SEEDED] = seeded
        }
    }

    val wordOfTheDay: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.WORD_OF_THE_DAY] ?: WOTD_FALLBACK
        }

    suspend fun setWordOfTheDay(word: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.WORD_OF_THE_DAY] = word
        }
    }

    val quizLength: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.QUIZ_LENGTH] ?: DEFAULT_QUIZ_LENGTH
        }

    suspend fun setQuizLength(length: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.QUIZ_LENGTH] = length
        }
    }

    val dailyQuizRefreshTime: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.DAILY_QUIZ_REFRESH_TIME] ?: DEFAULT_REFRESH_TIMESTAMP
        }

    suspend fun setDailyQuizRefreshTime(time: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DAILY_QUIZ_REFRESH_TIME] = time
        }
    }

    val lastCompletedAtTimestamp: Flow<Long?> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.LAST_COMPLETED_AT_TIMESTAMP]
        }

    val refreshTimeAtLastCompletion: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.REFRESH_TIME_AT_LAST_COMPLETION] ?: DEFAULT_REFRESH_TIMESTAMP
        }

    suspend fun saveQuizCompletion(timestamp: Long, refreshTimeSnapshot: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_COMPLETED_AT_TIMESTAMP] = timestamp
            preferences[PreferencesKeys.REFRESH_TIME_AT_LAST_COMPLETION] = refreshTimeSnapshot
        }
    }

    val themeMode: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.THEME_MODE] ?: DEFAULT_THEME
        }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode
        }
    }

    val fontSize: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.FONT_SIZE] ?: DEFAULT_FONT_SIZE
        }

    suspend fun setFontSize(size: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FONT_SIZE] = size
        }
    }

    suspend fun resetQuizCompletion() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.LAST_COMPLETED_AT_TIMESTAMP)
            preferences.remove(PreferencesKeys.REFRESH_TIME_AT_LAST_COMPLETION)
        }
    }
}