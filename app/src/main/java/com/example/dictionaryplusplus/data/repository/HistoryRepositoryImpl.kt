package com.example.dictionaryplusplus.data.repository

import com.example.dictionaryplusplus.data.local.dao.SeenEventDao
import com.example.dictionaryplusplus.data.local.entity.SeenEventEntity
import com.example.dictionaryplusplus.domain.model.SeenEvent
import com.example.dictionaryplusplus.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepositoryImpl @Inject constructor(
    private val seenEventDao: SeenEventDao
) : HistoryRepository {
    override fun observeSeenEvents(): Flow<List<SeenEvent>> {
        val flow = seenEventDao.getAllSeenEvents()

        return flow.map { list ->
            list.map { entity ->
                SeenEvent(
                    id = entity.id,
                    word = entity.word,
                    seenAtTimestamp = entity.seenAtTimestamp,
                    isConfirmed = entity.isConfirmed
                )
            }
        }
    }

    override suspend fun deleteSeenEvent(id: Long) {
        seenEventDao.deleteSeenEventById(id)
    }

    override suspend fun addSeenEvent(word: String) {
        seenEventDao.insertSeenEvent(
            SeenEventEntity(
                word = word,
                seenAtTimestamp = System.currentTimeMillis(),
                isConfirmed = true
            )
        )
    }
}
