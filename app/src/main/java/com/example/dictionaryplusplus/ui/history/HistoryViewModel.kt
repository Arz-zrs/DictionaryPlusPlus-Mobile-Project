package com.example.dictionaryplusplus.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryplusplus.domain.repository.HistoryRepository
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
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : ViewModel() {
    private val dateFormatter = DateTimeFormatter.ofPattern("dd MM yyyy, HH:mm", Locale.getDefault())
        .withZone(ZoneId.systemDefault())

    private val _currentFilter = MutableStateFlow("All")
    val currentFilter: StateFlow<String> = _currentFilter.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val historyList: StateFlow<List<SeenEventUiModel>> = _currentFilter
        .flatMapLatest { filter ->
            historyRepository.observeSeenEvents(filter)
        }
        .map { events ->
            events.map { event ->
                SeenEventUiModel(
                    id = event.id,
                    word = event.word,
                    formattedDate = dateFormatter.format(Instant.ofEpochMilli(event.seenAtTimestamp)),
                    masteryStatus = event.masteryStatus
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onFilterChanged(newFilter: String) {
        _currentFilter.value = newFilter
    }

    fun removeHistoryEntry(id: Long) {
        viewModelScope.launch {
            historyRepository.deleteSeenEvent(id)
        }
    }
}