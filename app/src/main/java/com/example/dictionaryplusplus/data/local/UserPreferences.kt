package com.example.dictionaryplusplus.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
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
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val IS_WORD_BANK_SEEDED = booleanPreferencesKey("is_word_bank_seeded")
        val IS_DEFINITION_SEEDED = booleanPreferencesKey("is_definition_seeded")
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
            preferences[PreferencesKeys.NOTIFICATION_TIME] ?: "08:00"
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
}