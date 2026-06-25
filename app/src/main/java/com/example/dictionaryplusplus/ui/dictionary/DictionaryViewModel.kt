package com.example.dictionaryplusplus.ui.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryplusplus.domain.model.Word
import com.example.dictionaryplusplus.domain.usecase.SearchWordsUseCase
import com.example.dictionaryplusplus.domain.usecase.SetSeenEventUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class DictionaryViewModel @Inject constructor(
    private val searchWordsUseCase: SearchWordsUseCase,
    private val setSeenEventUseCase: SetSeenEventUseCase
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow<DictionaryUiState>(DictionaryUiState.Hidden)
    val uiState: StateFlow<DictionaryUiState> = _uiState.asStateFlow()

    val searchResults: StateFlow<List<Word>> = _searchQuery
        .debounce(300.milliseconds)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) flowOf(emptyList())
            else searchWordsUseCase(query)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onWordSelected(word: String) {
        _uiState.value = DictionaryUiState.WordDetail(word)
        viewModelScope.launch {
            setSeenEventUseCase(word)
        }
    }

    fun onSheetDismissed() {
        _uiState.value = DictionaryUiState.Hidden
    }
}
