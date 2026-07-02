package com.example.dictionaryplusplus.ui.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryplusplus.domain.usecase.quiz.GetTotalParticipantCountUseCase
import com.example.dictionaryplusplus.domain.usecase.quiz.GetUserRankUseCase
import com.example.dictionaryplusplus.domain.usecase.quiz.ObserveLeaderboardUseCase
import com.example.dictionaryplusplus.domain.usecase.auth.ObserveUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val observeLeaderboardUseCase: ObserveLeaderboardUseCase,
    private val observeUserProfileUseCase: ObserveUserProfileUseCase,
    private val getUserRankUseCase: GetUserRankUseCase,
    private val getTotalParticipantCountUseCase: GetTotalParticipantCountUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeLeaderboardUseCase().collectLatest { leaderboardList ->
                _uiState.update { it.copy(leaderboardList = leaderboardList, isLoading = false) }
            }
        }

        viewModelScope.launch {
            combine(
                observeLeaderboardUseCase(),
                observeUserProfileUseCase()
            ) { _, userProfile -> userProfile }
                .debounce(300.milliseconds)
                .collectLatest { userProfile ->
                    val score = userProfile?.totalScore ?: return@collectLatest
                    val rank = getUserRankUseCase(score).getOrDefault(0)
                    val total = getTotalParticipantCountUseCase().getOrDefault(0).toInt()

                    _uiState.update {
                        it.copy(
                            currentUserRank = rank,
                            totalParticipants = total,
                            currentUserScore = score
                        )
                    }
                }
        }
    }
}