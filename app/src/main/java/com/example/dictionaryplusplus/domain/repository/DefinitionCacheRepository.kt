package com.example.dictionaryplusplus.domain.repository

import com.example.dictionaryplusplus.domain.mapper.DefinitionCache
import kotlinx.coroutines.flow.Flow

interface DefinitionCacheRepository {
    fun observeDefinition(word: String): Flow<DefinitionCache?>
}