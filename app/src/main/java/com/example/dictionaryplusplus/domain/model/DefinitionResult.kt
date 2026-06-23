package com.example.dictionaryplusplus.domain.model

sealed interface DefinitionResult {
    data class Success(val definition: Definition) : DefinitionResult
    data class Error(val type: DefinitionErrorType) : DefinitionResult
    data object Loading : DefinitionResult
}
