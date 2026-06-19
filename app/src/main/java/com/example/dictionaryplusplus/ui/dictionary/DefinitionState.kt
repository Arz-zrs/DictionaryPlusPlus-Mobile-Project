package com.example.dictionaryplusplus.ui.dictionary

import com.example.dictionaryplusplus.domain.mapper.DefinitionCache

interface DefinitionState {
    object Loading : DefinitionState
    data class Success(val definition: DefinitionCache) : DefinitionState
    data class Error(val message: String) : DefinitionState
    object NotCached : DefinitionState
}