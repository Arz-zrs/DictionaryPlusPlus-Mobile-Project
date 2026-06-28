package com.example.dictionaryplusplus.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryplusplus.domain.model.Definition
import com.example.dictionaryplusplus.domain.model.SeenEvent
import com.example.dictionaryplusplus.domain.usecase.words.EnsureWotdAvailableUseCase
import com.example.dictionaryplusplus.domain.usecase.words.ObserveIsFetchingWotdUseCase
import com.example.dictionaryplusplus.domain.usecase.auth.ObserveUserProfileUseCase
import com.example.dictionaryplusplus.domain.usecase.words.ObserveWordOfTheDayUseCase
import com.example.dictionaryplusplus.domain.usecase.words.ObserveSeenEventsUseCase
import com.example.dictionaryplusplus.domain.usecase.words.RefreshWordOfTheDayUseCase
import com.example.dictionaryplusplus.domain.usecase.words.SetSeenEventUseCase
import com.example.dictionaryplusplus.ui.history.HistoryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    observeUserProfileUseCase: ObserveUserProfileUseCase,
    observeWordOfTheDayUseCase: ObserveWordOfTheDayUseCase,
    observeSeenEventsUseCase: ObserveSeenEventsUseCase,
    observeIsFetchingWotdUseCase: ObserveIsFetchingWotdUseCase,
    private val ensureWotdAvailableUseCase: EnsureWotdAvailableUseCase,
    private val setSeenEventUseCase: SetSeenEventUseCase,
    private val refreshWordOfTheDayUseCase: RefreshWordOfTheDayUseCase
): ViewModel() {

    private val _sheetState = MutableStateFlow<DashboardSheetState>(DashboardSheetState.Hidden)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DashboardUiState> = combine(
        observeUserProfileUseCase().map { profile ->
            Pair(profile?.displayName ?: "", profile?.totalScore ?: 0)
        },
        observeWordOfTheDayUseCase(),
        observeIsFetchingWotdUseCase(),
        observeSeenEventsUseCase().map { it.take(5) },
        _sheetState
    ) { flows ->
        @Suppress("UNCHECKED_CAST")
        val userPair = flows[0] as Pair<String, Int>
        val wotd = flows[1] as? Definition
        val isFetching = flows[2] as Boolean
        @Suppress("UNCHECKED_CAST")
        val recentList = flows[3] as List<SeenEvent>
        val sheetState = flows[4] as DashboardSheetState

        val wotdState = when {
            wotd != null -> WotdState.Available(wotd)
            isFetching -> WotdState.Loading
            else -> WotdState.Unavailable
        }

        DashboardUiState(
            displayName = userPair.first,
            userScore = userPair.second,
            wordOfTheDay = wotdState,
            isFetchingWotd = isFetching,
            recentWords = recentList.map { event ->
                HistoryUiState.fromDomain(
                    id = event.id,
                    word = event.word,
                    timestamp = event.seenAtTimestamp
                )
            },
            sheetState = sheetState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    init {
        viewModelScope.launch {
            ensureWotdAvailableUseCase()
        }
    }

    fun onWotdClicked(word: String) {
        viewModelScope.launch {
            setSeenEventUseCase(word)
        }
        _sheetState.value = DashboardSheetState.WordDetail(word)
    }

    fun onRefreshWotdClicked() {
        viewModelScope.launch {
            refreshWordOfTheDayUseCase()
        }
    }

    fun onSheetDismissed() {
        _sheetState.value = DashboardSheetState.Hidden
    }

    fun onRecentWordClicked(word: String) {
        _sheetState.value = DashboardSheetState.WordDetail(word)
    }
}
