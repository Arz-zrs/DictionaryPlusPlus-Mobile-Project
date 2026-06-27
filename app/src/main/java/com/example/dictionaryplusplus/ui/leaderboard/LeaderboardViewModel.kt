package com.example.dictionaryplusplus.ui.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryplusplus.domain.usecase.GetTotalParticipantCountUseCase
import com.example.dictionaryplusplus.domain.usecase.GetUserRankUseCase
import com.example.dictionaryplusplus.domain.usecase.ObserveLeaderboardUseCase
import com.example.dictionaryplusplus.domain.usecase.ObserveUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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
            combine(
                observeLeaderboardUseCase(),
                observeUserProfileUseCase()
            ) { leaderboardList, userProfile ->
                Pair(leaderboardList, userProfile)
            }.collectLatest { (leaderboardList, userProfile) ->
                _uiState.update { it.copy(leaderboardList = leaderboardList, isLoading = false) }
                val score = userProfile?.totalScore ?: return@collectLatest
                val rank = getUserRankUseCase(score).getOrDefault(0)
                val total = getTotalParticipantCountUseCase().getOrDefault(0).toInt()

                _uiState.update { it.copy(
                    currentUserRank = rank,
                    totalParticipants = total,
                    currentUserScore = score
                )}
            }
        }
    }
}