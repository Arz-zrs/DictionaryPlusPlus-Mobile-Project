package com.example.dictionaryplusplus.domain.usecase.setting

import com.example.dictionaryplusplus.domain.model.ThemeMode
import com.example.dictionaryplusplus.domain.repository.SettingsRepository
import javax.inject.Inject

class SetThemeModeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(mode: ThemeMode) = settingsRepository.setThemeMode(mode)
}
