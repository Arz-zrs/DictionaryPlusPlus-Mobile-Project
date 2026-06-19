package com.example.dictionaryplusplus.ui.dictionary.components

import com.example.dictionaryplusplus.ui.dictionary.DefinitionState

data class WordDetailUiState(
    val word: String = "",
    val definitionState: DefinitionState = DefinitionState.Loading,
    val noteText: String = "",
    val isFavourite: Boolean = false
)