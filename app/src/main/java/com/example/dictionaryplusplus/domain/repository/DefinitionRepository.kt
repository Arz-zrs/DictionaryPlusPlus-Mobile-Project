package com.example.dictionaryplusplus.domain.repository

import com.example.dictionaryplusplus.domain.model.Definition
import kotlinx.coroutines.flow.Flow

interface DefinitionRepository {
    fun observeDefinition(word: String): Flow<Definition?>
    suspend fun getDefinition(word: String): Result<Definition>
}