package com.example.dictionaryplusplus.ui.dictionary

data class WordDetailUiState(
    val word: String = "",
    val definitionState: DefinitionState = DefinitionState.Loading,
    val noteText: String = "",
    val isFavourite: Boolean = false
)