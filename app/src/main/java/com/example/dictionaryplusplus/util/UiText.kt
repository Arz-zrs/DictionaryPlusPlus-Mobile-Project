package com.example.dictionaryplusplus.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class UiText {
    data class DynamicString(val value: String): UiText()
    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any
    ): UiText()

    @Composable
    fun asString(): String {
        return when(this) {
            is DynamicString -> value
            is StringResource -> stringResource(resId, *args)
        }
    }
}

fun Throwable.asUiText(defaultResId: Int): UiText {
    return this.message?.let { UiText.DynamicString(it) } ?: UiText.StringResource(defaultResId)
}