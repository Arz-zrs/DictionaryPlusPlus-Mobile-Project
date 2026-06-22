package com.example.dictionaryplusplus.ui.dashboard

import com.example.dictionaryplusplus.domain.model.Definition

sealed interface WotdState {
    data object Loading: WotdState
    data class Available(val definition: Definition): WotdState
    data object Unavailable: WotdState
}