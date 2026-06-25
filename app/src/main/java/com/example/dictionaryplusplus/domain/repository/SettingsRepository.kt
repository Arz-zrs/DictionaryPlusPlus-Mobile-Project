package com.example.dictionaryplusplus.domain.repository

import com.example.dictionaryplusplus.domain.model.FontSize
import com.example.dictionaryplusplus.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getThemeMode(): Flow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode)
    fun getFontSize(): Flow<FontSize>
    suspend fun setFontSize(size: FontSize)
}
