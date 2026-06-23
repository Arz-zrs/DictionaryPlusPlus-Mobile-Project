package com.example.dictionaryplusplus.core.util

import androidx.annotation.StringRes

sealed interface ErrorMessage {
    data class Known(@StringRes val messageRes: Int) : ErrorMessage
    data object None : ErrorMessage
}

fun asErrorMessage(@StringRes defaultResId: Int): ErrorMessage {
    return ErrorMessage.Known(defaultResId)
}
