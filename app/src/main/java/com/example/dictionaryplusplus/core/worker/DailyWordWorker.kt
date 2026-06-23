package com.example.dictionaryplusplus.core.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.dictionaryplusplus.data.local.dao.SeenEventDao
import com.example.dictionaryplusplus.data.local.dao.WordDao
import com.example.dictionaryplusplus.data.local.entity.SeenEventEntity
import com.example.dictionaryplusplus.domain.model.DefinitionResult
import com.example.dictionaryplusplus.domain.model.DefinitionErrorType
import com.example.dictionaryplusplus.domain.model.MasteryStatus
import com.example.dictionaryplusplus.domain.repository.DefinitionRepository
import com.example.dictionaryplusplus.core.notification.NotificationBuilder
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DailyWordWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val wordDao: WordDao,
    private val seenEventDao: SeenEventDao,
    private val definitionRepository: DefinitionRepository,
    private val notificationBuilder: NotificationBuilder
): CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            val wordEntity = wordDao.getRandomUnseenWord() ?: return Result.failure()
            val word = wordEntity.word

            val definitionResult = definitionRepository.getDefinition(word)
            
            val definition = when (definitionResult) {
                is DefinitionResult.Success -> definitionResult.definition
                is DefinitionResult.Error -> {
                    return if (definitionResult.type == DefinitionErrorType.NOT_FOUND) {
                        Result.failure()
                    } else {
                        Result.retry()
                    }
                }
                DefinitionResult.Loading -> return Result.retry()
            }

            val seenEventId = seenEventDao.insertSeenEvent(
                SeenEventEntity(
                    word = word,
                    seenAtTimestamp = System.currentTimeMillis(),
                    isConfirmed = false,
                    masteryStatus = MasteryStatus.LEARNING
                )
            )

            notificationBuilder.showDailyNotification(
                eventId = seenEventId,
                word = word,
                phonetic = definition.phonetic ?: "",
                shortDefinition = definition.definition
            )

            Result.success()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Result.retry()
        }
    }
}