package com.example.dictionaryplusplus.ui.dictionary.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryplusplus.data.remote.ApiResponse
import com.example.dictionaryplusplus.data.remote.ErrorType
import com.example.dictionaryplusplus.domain.repository.DefinitionRepository
import com.example.dictionaryplusplus.domain.repository.FavouriteRepository
import com.example.dictionaryplusplus.domain.repository.WordNoteRepository
import com.example.dictionaryplusplus.domain.usecase.GetDefinitionUseCase
import com.example.dictionaryplusplus.ui.dictionary.DefinitionState
import com.example.dictionaryplusplus.util.ErrorMessage
import com.example.dictionaryplusplus.R
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
    private val wordNoteRepository: WordNoteRepository,
    private val getDefinitionUseCase: GetDefinitionUseCase
) : ViewModel() {
    private val _currentWord = MutableStateFlow("")
    private val _definitionError = MutableStateFlow<ErrorMessage>(ErrorMessage.None)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<WordDetailUiState> = _currentWord
        .filter { it.isNotEmpty() }
        .flatMapLatest { word ->
            combine(
                definitionRepository.observeDefinition(word),
                wordNoteRepository.observeWordNote(word),
                favouriteRepository.observeIsFavourite(word),
                _definitionError
            ) { definition, note, isFavourite, error ->
                val definitionState = when {
                    definition != null -> DefinitionState.Success(definition)
                    error !is ErrorMessage.None -> DefinitionState.Error(error)
                    else -> DefinitionState.Loading
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
        _definitionError.value = ErrorMessage.None
        _currentWord.value = word
        viewModelScope.launch {
            val response = getDefinitionUseCase(word)
            if (response is ApiResponse.Error) {
                _definitionError.value = mapErrorTypeToMessage(response.errorType)
            }
        }
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

    private fun mapErrorTypeToMessage(errorType: ErrorType): ErrorMessage {
        return when (errorType) {
            ErrorType.NO_INTERNET -> ErrorMessage.Known(R.string.error_no_internet)
            ErrorType.TIMEOUT -> ErrorMessage.Known(R.string.error_timeout)
            ErrorType.NOT_FOUND -> ErrorMessage.Known(R.string.error_word_not_found)
            ErrorType.UNKNOWN -> ErrorMessage.Known(R.string.error_unknown)
        }
    }
}
