package com.example.dictionaryplusplus.domain.usecase.setting

import com.example.dictionaryplusplus.domain.model.ThemeMode
import com.example.dictionaryplusplus.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetThemeModeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<ThemeMode> = settingsRepository.getThemeMode()
}
