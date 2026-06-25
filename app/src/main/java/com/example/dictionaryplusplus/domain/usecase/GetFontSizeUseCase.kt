package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.domain.model.FontSize
import com.example.dictionaryplusplus.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFontSizeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<FontSize> = settingsRepository.getFontSize()
}
