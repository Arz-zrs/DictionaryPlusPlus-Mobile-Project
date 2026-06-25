package com.example.dictionaryplusplus.core.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.dictionaryplusplus.BuildConfig
import com.example.dictionaryplusplus.data.remote.WordnikApiService
import com.example.dictionaryplusplus.data.local.dao.SeenEventDao
import com.example.dictionaryplusplus.data.local.entity.SeenEventEntity
import com.example.dictionaryplusplus.domain.repository.WotdRepository
import com.example.dictionaryplusplus.core.notification.NotificationBuilder
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class WotdApiWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val wotdRepository: WotdRepository,
    private val wordnikApiService: WordnikApiService,
    private val seenEventDao: SeenEventDao,
    private val notificationBuilder: NotificationBuilder
): CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            val apiKey = BuildConfig.WORDNIK_API_KEY
            if (apiKey.isBlank()) {
                FirebaseCrashlytics.getInstance().recordException(Exception("API key is blank or not detected"))
                return Result.failure()
            }

            val response = wordnikApiService.fetchWordOfTheDay(apiKey)
            val word = response.word.trim().lowercase()
            val wordnikDefinition = response.definitions?.firstOrNull()?.text
            if (word.isEmpty() || wordnikDefinition.isNullOrBlank())
                return Result.failure()

            wotdRepository.setWordnikWordOfTheDay(word, wordnikDefinition)

            val seenEventId = seenEventDao.insertSeenEvent(
                SeenEventEntity(
                    word = word,
                    seenAtTimestamp = System.currentTimeMillis(),
                    isConfirmed = false
                )
            )

            notificationBuilder.showDailyNotification(
                eventId = seenEventId,
                word = word,
                phonetic = "",
                shortDefinition = wordnikDefinition
            )

            Result.success()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Result.retry()
        }
    }
}
