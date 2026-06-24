package com.example.dictionaryplusplus.ui.history

import com.example.dictionaryplusplus.domain.model.MasteryStatus
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class HistoryUiState(
    val id: Long,
    val word: String,
    val formattedDate: String,
    val masteryStatus: MasteryStatus
) {
    companion object {
        const val DEFAULT_PATTERN = "dd MMM yyyy, HH:mm"
        fun fromDomain(
            id: Long,
            word: String,
            timestamp: Long,
            masteryStatus: MasteryStatus,
            pattern: String = DEFAULT_PATTERN
        ): HistoryUiState {
            val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
                .withZone(ZoneId.systemDefault())
            return HistoryUiState(
                id = id,
                word = word,
                formattedDate = formatter.format(Instant.ofEpochMilli(timestamp)),
                masteryStatus = masteryStatus
            )
        }
    }
}
