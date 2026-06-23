package com.example.dictionaryplusplus.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryplusplus.domain.usecase.ObserveQuizAvailabilityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class QuizHubViewModel @Inject constructor(
    observeQuizAvailabilityUseCase: ObserveQuizAvailabilityUseCase
) : ViewModel() {
    val uiState: StateFlow<QuizHubUiState> = observeQuizAvailabilityUseCase()
        .map { QuizHubUiState(isDailyQuizAvailable = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = QuizHubUiState()
        )
}
