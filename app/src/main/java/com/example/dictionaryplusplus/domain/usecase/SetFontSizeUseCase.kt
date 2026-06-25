package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.domain.model.FontSize
import com.example.dictionaryplusplus.domain.repository.SettingsRepository
import javax.inject.Inject

class SetFontSizeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(size: FontSize) = settingsRepository.setFontSize(size)
}
