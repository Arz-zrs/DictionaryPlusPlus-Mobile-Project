package com.example.dictionaryplusplus.ui.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryplusplus.domain.model.DefinitionResult
import com.example.dictionaryplusplus.domain.model.DefinitionErrorType
import com.example.dictionaryplusplus.domain.usecase.words.GetDefinitionUseCase
import com.example.dictionaryplusplus.domain.usecase.words.ObserveDefinitionUseCase
import com.example.dictionaryplusplus.domain.usecase.words.ObserveIsFavouriteUseCase
import com.example.dictionaryplusplus.domain.usecase.words.ObserveWordNoteUseCase
import com.example.dictionaryplusplus.domain.usecase.words.SaveWordNoteUseCase
import com.example.dictionaryplusplus.domain.usecase.words.ToggleFavouriteUseCase
import com.example.dictionaryplusplus.core.util.ErrorMessage
import com.example.dictionaryplusplus.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@HiltViewModel
class WordDetailViewModel @Inject constructor(
    private val observeDefinitionUseCase: ObserveDefinitionUseCase,
    private val observeWordNoteUseCase: ObserveWordNoteUseCase,
    private val observeIsFavouriteUseCase: ObserveIsFavouriteUseCase,
    private val getDefinitionUseCase: GetDefinitionUseCase,
    private val saveWordNoteUseCase: SaveWordNoteUseCase,
    private val toggleFavouriteUseCase: ToggleFavouriteUseCase
) : ViewModel() {
    private val _currentWord = MutableStateFlow("")
    private val _definitionError = MutableStateFlow<ErrorMessage>(ErrorMessage.None)
    private val _noteInput = MutableStateFlow<NoteUpdate?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<WordDetailUiState> = _currentWord
        .filter { it.isNotEmpty() }
        .flatMapLatest { word ->
            combine(
                observeDefinitionUseCase(word),
                observeWordNoteUseCase(word),
                observeIsFavouriteUseCase(word),
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

    init {
        _noteInput
            .filterNotNull()
            .debounce(500.milliseconds)
            .onEach { update -> saveWordNote(update.note) }
            .launchIn(viewModelScope)
    }

    fun onNoteChanged(note: String) {
        val word = _currentWord.value
        if (word.isNotEmpty()) {
            _noteInput.value = NoteUpdate(word, note)
        }
    }

    fun loadWordDetails(word: String) {
        _definitionError.value = ErrorMessage.None
        _currentWord.value = word
        _noteInput.value = null

        viewModelScope.launch {
            val response = getDefinitionUseCase(word)
            if (response is DefinitionResult.Error) {
                _definitionError.value = mapErrorTypeToMessage(response.type)
            }
        }
    }

    fun saveWordNote(note: String) {
        val word = _currentWord.value
        if (word.isNotEmpty()) {
            viewModelScope.launch {
                saveWordNoteUseCase(word, note)
            }
        }
    }

    fun toggleFavourite() {
        val word = _currentWord.value
        if (word.isNotEmpty()) {
            viewModelScope.launch {
                toggleFavouriteUseCase(word)
            }
        }
    }

    private fun mapErrorTypeToMessage(errorType: DefinitionErrorType): ErrorMessage {
        return when (errorType) {
            DefinitionErrorType.NO_INTERNET -> ErrorMessage.Known(R.string.error_no_internet)
            DefinitionErrorType.TIMEOUT -> ErrorMessage.Known(R.string.error_timeout)
            DefinitionErrorType.NOT_FOUND -> ErrorMessage.Known(R.string.error_word_not_found)
            DefinitionErrorType.UNKNOWN -> ErrorMessage.Known(R.string.error_unknown)
        }
    }
}
