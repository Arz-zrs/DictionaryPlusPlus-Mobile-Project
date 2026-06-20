package com.example.dictionaryplusplus.ui.dictionary

import com.example.dictionaryplusplus.domain.mapper.Definition

interface DefinitionState {
    object Loading : DefinitionState
    data class Success(val definition: Definition) : DefinitionState
    data class Error(val message: String) : DefinitionState
    object NotCached : DefinitionState
}