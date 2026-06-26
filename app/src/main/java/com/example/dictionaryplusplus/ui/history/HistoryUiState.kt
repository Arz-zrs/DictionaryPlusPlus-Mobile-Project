package com.example.dictionaryplusplus.ui.history

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class HistoryUiState(
    val id: Long,
    val word: String,
    val formattedDate: String
) {
    companion object {
        const val DEFAULT_PATTERN = "dd MMM yyyy, HH:mm:ss"
        fun fromDomain(
            id: Long,
            word: String,
            timestamp: Long,
            pattern: String = DEFAULT_PATTERN
        ): HistoryUiState {
            val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
                .withZone(ZoneId.systemDefault())
            return HistoryUiState(
                id = id,
                word = word,
                formattedDate = formatter.format(Instant.ofEpochMilli(timestamp))
            )
        }
    }
}
