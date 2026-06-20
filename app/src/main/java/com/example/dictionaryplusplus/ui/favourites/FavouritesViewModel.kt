package com.example.dictionaryplusplus.ui.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryplusplus.domain.mapper.FavouriteWord
import com.example.dictionaryplusplus.domain.repository.FavouriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val favouriteRepository: FavouriteRepository
) : ViewModel() {
    val favouriteWords: StateFlow<List<FavouriteWord>> = favouriteRepository
        .observeFavouriteWord()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow<FavouriteUiState>(FavouriteUiState.Hidden)
    val uiState: StateFlow<FavouriteUiState> = _uiState.asStateFlow()

    fun unfavourite(word: String) {
        viewModelScope.launch {
            favouriteRepository.toggleFavourite(word)
        }
    }

    fun onWordSelected(word: String) {
        _uiState.value = FavouriteUiState.WordDetail(word)
    }

    fun onSheetDismissed() {
        _uiState.value = FavouriteUiState.Hidden
    }
}
