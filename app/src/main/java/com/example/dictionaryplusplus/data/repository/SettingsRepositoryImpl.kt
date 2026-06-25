package com.example.dictionaryplusplus.data.repository

import com.example.dictionaryplusplus.data.local.UserPreferences
import com.example.dictionaryplusplus.domain.model.FontSize
import com.example.dictionaryplusplus.domain.model.ThemeMode
import com.example.dictionaryplusplus.domain.repository.SettingsRepository
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val userPreferences: UserPreferences
) : SettingsRepository {

    override fun getThemeMode(): Flow<ThemeMode> {
        return userPreferences.themeMode.map { modeName ->
            try {
                ThemeMode.valueOf(modeName)
            } catch (e: IllegalArgumentException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                ThemeMode.SYSTEM
            }
        }
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        userPreferences.setThemeMode(mode.name)
    }

    override fun getFontSize(): Flow<FontSize> {
        return userPreferences.fontSize.map { sizeName ->
            try {
                FontSize.valueOf(sizeName)
            } catch (e: IllegalArgumentException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                FontSize.MEDIUM
            }
        }
    }

    override suspend fun setFontSize(size: FontSize) {
        userPreferences.setFontSize(size.name)
    }
}
