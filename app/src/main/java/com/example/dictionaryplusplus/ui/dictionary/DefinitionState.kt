package com.example.dictionaryplusplus.ui.dictionary

import com.example.dictionaryplusplus.domain.mapper.Definition
import com.example.dictionaryplusplus.util.ErrorMessage

sealed interface DefinitionState {
    data object Loading : DefinitionState
    data class Success(val definition: Definition) : DefinitionState
    data class Error(val errorMessage: ErrorMessage) : DefinitionState
    data object NotCached : DefinitionState
}
