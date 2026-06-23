package com.example.dictionaryplusplus.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryplusplus.domain.model.HistoryFilter
import com.example.dictionaryplusplus.domain.usecase.ObserveSeenEventsUseCase
import com.example.dictionaryplusplus.domain.usecase.DeleteSeenEventUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val observeSeenEventsUseCase: ObserveSeenEventsUseCase,
    private val deleteSeenEventUseCase: DeleteSeenEventUseCase
) : ViewModel() {

    private val _currentFilter = MutableStateFlow(HistoryFilter.ALL)
    val currentFilter: StateFlow<HistoryFilter> = _currentFilter.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val historyList: StateFlow<List<SeenEventUiModel>> = _currentFilter
        .flatMapLatest { filter ->
            observeSeenEventsUseCase(filter)
        }
        .map { events ->
            events.map { event ->
                SeenEventUiModel.fromDomain(
                    id = event.id,
                    word = event.word,
                    timestamp = event.seenAtTimestamp,
                    masteryStatus = event.masteryStatus,
                    pattern = "dd MM yyyy, HH:mm"
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onFilterChanged(newFilter: HistoryFilter) {
        _currentFilter.value = newFilter
    }

    fun removeHistoryEntry(id: Long) {
        viewModelScope.launch {
            deleteSeenEventUseCase(id)
        }
    }
}
