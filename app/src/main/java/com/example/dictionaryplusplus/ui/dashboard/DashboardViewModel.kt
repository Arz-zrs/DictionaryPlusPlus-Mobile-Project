package com.example.dictionaryplusplus.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryplusplus.domain.repository.HistoryRepository
import com.example.dictionaryplusplus.domain.repository.UserRepository
import com.example.dictionaryplusplus.domain.repository.WotdRepository
import com.example.dictionaryplusplus.ui.history.SeenEventUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    userRepository: UserRepository,
    wotdRepository: WotdRepository,
    historyRepository: HistoryRepository
): ViewModel() {

    private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
        .withZone(ZoneId.systemDefault())


    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DashboardUiState> = combine(
        userRepository.observeUserProfile().map { it?.totalScore ?: 0 },
        wotdRepository.observeWordOfTheDay(),
        historyRepository.observeSeenEvents("All").map { it.take(5) }
    ) { score, wotd, recentList ->
        DashboardUiState(
            userScore = score,
            wordOfTheDay = wotd,
            recentWords = recentList.map { event ->
                SeenEventUiModel(
                    id = event.id,
                    word = event.word,
                    formattedDate = dateFormatter.format(Instant.ofEpochMilli(event.seenAtTimestamp)),
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
