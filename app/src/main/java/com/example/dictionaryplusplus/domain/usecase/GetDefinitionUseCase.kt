package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.domain.model.Definition
import com.example.dictionaryplusplus.domain.repository.DefinitionRepository
import javax.inject.Inject

class GetDefinitionUseCase @Inject constructor(
    private val definitionRepository: DefinitionRepository
) {
    suspend operator fun invoke(word: String): Result<Definition> {
        val cleanedWord = word.trim().lowercase()
        return if (cleanedWord.isBlank()) {
            Result.failure(IllegalArgumentException("Word cannot be blank"))
        } else definitionRepository.getDefinition(cleanedWord)
    }
}