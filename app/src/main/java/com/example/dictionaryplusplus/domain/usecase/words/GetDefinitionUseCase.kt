package com.example.dictionaryplusplus.domain.usecase.words

import com.example.dictionaryplusplus.domain.model.DefinitionResult
import com.example.dictionaryplusplus.domain.model.DefinitionErrorType
import com.example.dictionaryplusplus.domain.repository.DefinitionRepository
import javax.inject.Inject

class GetDefinitionUseCase @Inject constructor(
    private val definitionRepository: DefinitionRepository
) {
    suspend operator fun invoke(word: String): DefinitionResult {
        val cleanedWord = word.trim().lowercase()
        return if (cleanedWord.isBlank()) {
            DefinitionResult.Error(DefinitionErrorType.UNKNOWN)
        } else definitionRepository.getDefinition(cleanedWord)
    }
}