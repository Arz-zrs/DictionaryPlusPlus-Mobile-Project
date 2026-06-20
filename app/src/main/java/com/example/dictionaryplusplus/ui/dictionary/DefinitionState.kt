package com.example.dictionaryplusplus.ui.dictionary

import com.example.dictionaryplusplus.domain.mapper.Definition
import com.example.dictionaryplusplus.util.UiText

interface DefinitionState {
    object Loading : DefinitionState
    data class Success(val definition: Definition) : DefinitionState
    data class Error(val message: UiText) : DefinitionState
    object NotCached : DefinitionState
}
