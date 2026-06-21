package com.example.dictionaryplusplus.domain.repository

import com.example.dictionaryplusplus.domain.model.SeenEvent
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun observeSeenEvents(filter: String): Flow<List<SeenEvent>>
    suspend fun deleteSeenEvent(id: Long)
}