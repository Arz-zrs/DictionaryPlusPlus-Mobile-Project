package com.example.dictionaryplusplus.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryplusplus.data.local.UserPreferences
import com.example.dictionaryplusplus.domain.usecase.ObserveQuizAvailabilityUseCase
import com.example.dictionaryplusplus.domain.usecase.ObserveUserProfileUseCase
import com.example.dictionaryplusplus.domain.usecase.ObserveWordOfTheDayUseCase
import com.example.dictionaryplusplus.domain.usecase.ObserveSeenEventsUseCase
import com.example.dictionaryplusplus.domain.usecase.SetSeenEventUseCase
import com.example.dictionaryplusplus.domain.usecase.TriggerWotdWorkerUseCase
import com.example.dictionaryplusplus.ui.history.HistoryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    observeUserProfileUseCase: ObserveUserProfileUseCase,
    observeWordOfTheDayUseCase: ObserveWordOfTheDayUseCase,
    observeSeenEventsUseCase: ObserveSeenEventsUseCase,
    observeQuizAvailabilityUseCase: ObserveQuizAvailabilityUseCase,
    private val triggerWotdWorkerUseCase: TriggerWotdWorkerUseCase,
    private val userPreferences: UserPreferences,
    private val setSeenEventUseCase: SetSeenEventUseCase
): ViewModel() {

    private val _sheetState = MutableStateFlow<DashboardSheetState>(DashboardSheetState.Hidden)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DashboardUiState> = combine(
        observeUserProfileUseCase().map { profile ->
            Pair(profile?.displayName ?: "", profile?.totalScore ?: 0)
        },
        observeWordOfTheDayUseCase().map { definition ->
            if (definition != null) WotdState.Available(definition)
            else WotdState.Unavailable
        },
        observeSeenEventsUseCase().map { it.take(5) },
        observeQuizAvailabilityUseCase(),
        _sheetState
    ) { (displayName, score), wotd, recentList, isQuizAvailable, sheetState ->
        DashboardUiState(
            displayName = displayName,
            userScore = score,
            wordOfTheDay = wotd,
            recentWords = recentList.map { event ->
                HistoryUiState.fromDomain(
                    id = event.id,
                    word = event.word,
                    timestamp = event.seenAtTimestamp
                )
            },
            isQuizAvailable = isQuizAvailable,
            sheetState = sheetState
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardUiState()
        )

    init {
        viewModelScope.launch {
            val currentWord = userPreferences.wordOfTheDay.first()
            if (currentWord == UserPreferences.WOTD_FALLBACK || currentWord.isBlank()) {
                triggerWotdWorkerUseCase()
            }
        }
    }

    fun onWotdClicked(word: String) {
        viewModelScope.launch {
            setSeenEventUseCase(word)
        }
        _sheetState.value = DashboardSheetState.WordDetail(word)
    }

    fun onSheetDismissed() {
        _sheetState.value = DashboardSheetState.Hidden
    }

    fun onRecentWordClicked(word: String) {
        _sheetState.value = DashboardSheetState.WordDetail(word)
    }
}
