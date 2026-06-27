package com.example.dictionaryplusplus.domain.usecase.words

import com.example.dictionaryplusplus.domain.model.Definition
import com.example.dictionaryplusplus.domain.repository.DefinitionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveDefinitionUseCase @Inject constructor(
    private val definitionRepository: DefinitionRepository
) {
    operator fun invoke(word: String): Flow<Definition?> {
        return definitionRepository.observeDefinition(word)
    }
}
