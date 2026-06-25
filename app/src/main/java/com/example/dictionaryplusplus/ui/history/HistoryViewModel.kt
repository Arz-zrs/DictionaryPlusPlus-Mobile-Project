package com.example.dictionaryplusplus.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryplusplus.domain.usecase.ObserveSeenEventsUseCase
import com.example.dictionaryplusplus.domain.usecase.DeleteSeenEventUseCase
import com.example.dictionaryplusplus.domain.usecase.ToggleFavouriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
    private val _toastMessage = Channel<String>()
    val toastMessage = _toastMessage.receiveAsFlow()

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
            _toastMessage.send("Removed $word from history")
        }
    }

    fun toggleFavourite(word: String) {
        viewModelScope.launch {
            toggleFavouriteUseCase(word)
            _toastMessage.send("Favourite status updated for $word")
        }
    }
}
