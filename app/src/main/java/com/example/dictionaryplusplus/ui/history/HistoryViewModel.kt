package com.example.dictionaryplusplus.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryplusplus.domain.usecase.words.ObserveSeenEventsUseCase
import com.example.dictionaryplusplus.domain.usecase.words.DeleteSeenEventUseCase
import com.example.dictionaryplusplus.domain.usecase.words.ToggleFavouriteUseCase
import com.example.dictionaryplusplus.util.UiText
import com.example.dictionaryplusplus.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    observeSeenEventsUseCase: ObserveSeenEventsUseCase,
    private val deleteSeenEventUseCase: DeleteSeenEventUseCase,
    private val toggleFavouriteUseCase: ToggleFavouriteUseCase
) : ViewModel() {
    private val _toastMessage = Channel<UiText>()
    val toastMessage = _toastMessage.receiveAsFlow()
    private val _uiState = MutableStateFlow<HistorySheetState>(HistorySheetState.Hidden)
    val uiState: StateFlow<HistorySheetState> = _uiState.asStateFlow()

    val historyList: StateFlow<List<HistoryUiState>> = observeSeenEventsUseCase()
        .map { events ->
            events.map { event ->
                HistoryUiState.fromDomain(
                    id = event.id,
                    word = event.word,
                    timestamp = event.seenAtTimestamp
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun removeHistoryEntry(id: Long, word: String) {
        viewModelScope.launch {
            deleteSeenEventUseCase(id)
            _toastMessage.send(UiText.StringResource(R.string.history_removed_message, word))
        }
    }

    fun toggleFavourite(word: String) {
        viewModelScope.launch {
            val isAdded = toggleFavouriteUseCase(word)
            val resId = if (isAdded) R.string.favourite_added_message else R.string.favourite_removed_message
            _toastMessage.send(UiText.StringResource(resId, word))
        }
    }

    fun onWordSelected(word: String) {
        _uiState.value = HistorySheetState.WordDetail(word)
    }

    fun onSheetDismissed() {
        _uiState.value = HistorySheetState.Hidden
    }
}
