package com.example.dictionaryplusplus.domain.usecase

import com.example.dictionaryplusplus.data.remote.ApiResponse
import com.example.dictionaryplusplus.data.remote.ErrorType
import com.example.dictionaryplusplus.domain.model.Definition
import com.example.dictionaryplusplus.domain.repository.DefinitionRepository
import javax.inject.Inject

class GetDefinitionUseCase @Inject constructor(
    private val definitionRepository: DefinitionRepository
) {
    suspend operator fun invoke(word: String): ApiResponse<Definition> {
        val cleanedWord = word.trim().lowercase()
        return if (cleanedWord.isBlank()) {
            ApiResponse.Error(ErrorType.UNKNOWN)
        } else definitionRepository.getDefinition(cleanedWord)
    }
}