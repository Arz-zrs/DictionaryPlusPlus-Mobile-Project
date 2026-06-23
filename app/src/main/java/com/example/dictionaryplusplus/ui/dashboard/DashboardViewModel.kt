package com.example.dictionaryplusplus.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryplusplus.domain.model.HistoryFilter
import com.example.dictionaryplusplus.domain.usecase.ObserveUserProfileUseCase
import com.example.dictionaryplusplus.domain.usecase.ObserveWordOfTheDayUseCase
import com.example.dictionaryplusplus.domain.usecase.ObserveSeenEventsUseCase
import com.example.dictionaryplusplus.ui.history.HistoryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    observeUserProfileUseCase: ObserveUserProfileUseCase,
    observeWordOfTheDayUseCase: ObserveWordOfTheDayUseCase,
    observeSeenEventsUseCase: ObserveSeenEventsUseCase
): ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DashboardUiState> = combine(
        observeUserProfileUseCase().map { it?.totalScore ?: 0 },
        observeWordOfTheDayUseCase().map { definition ->
            if (definition != null) WotdState.Available(definition)
            else WotdState.Unavailable
        },
        observeSeenEventsUseCase(HistoryFilter.ALL).map { it.take(5) }
    ) { score, wotd, recentList ->
        DashboardUiState(
            userScore = score,
            wordOfTheDay = wotd,
            recentWords = recentList.map { event ->
                HistoryUiState.fromDomain(
                    id = event.id,
                    word = event.word,
                    timestamp = event.seenAtTimestamp,
                    masteryStatus = event.masteryStatus
                )
            }
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardUiState()
        )
}
