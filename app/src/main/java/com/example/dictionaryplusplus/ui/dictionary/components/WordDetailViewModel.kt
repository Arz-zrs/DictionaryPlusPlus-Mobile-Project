package com.example.dictionaryplusplus.ui.dictionary.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryplusplus.domain.repository.DefinitionRepository
import com.example.dictionaryplusplus.domain.repository.FavouriteRepository
import com.example.dictionaryplusplus.domain.repository.WordNoteRepository
import com.example.dictionaryplusplus.ui.dictionary.DefinitionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordDetailViewModel @Inject constructor(
    private val definitionRepository: DefinitionRepository,
    private val favouriteRepository: FavouriteRepository,
    private val wordNoteRepository: WordNoteRepository
) : ViewModel() {
    private val _currentWord = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<WordDetailUiState> = _currentWord
        .filter { it.isNotEmpty() }
        .flatMapLatest { word ->
            combine(
                definitionRepository.observeDefinition(word),
                wordNoteRepository.observeWordNote(word),
                favouriteRepository.observeIsFavourite(word)
            ) { definition, note, isFavourite ->
                val definitionState = if (definition != null) {
                    DefinitionState.Success(definition)
                } else {
                    DefinitionState.NotCached
                }

                WordDetailUiState(
                    word = word,
                    definitionState = definitionState,
                    noteText = note,
                    isFavourite = isFavourite
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = WordDetailUiState()
        )

    fun loadWordDetails(word: String) {
        _currentWord.value = word
    }

    fun saveWordNote(note: String) {
        val word = _currentWord.value
        if (word.isNotEmpty()) {
            viewModelScope.launch {
                wordNoteRepository.saveWordNote(word, note)
            }
        }
    }

    fun toggleFavourite() {
        val word = _currentWord.value
        if (word.isNotEmpty()) {
            viewModelScope.launch {
                favouriteRepository.toggleFavourite(word)
            }
        }
    }
}