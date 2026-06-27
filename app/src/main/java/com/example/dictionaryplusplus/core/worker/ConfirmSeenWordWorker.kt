package com.example.dictionaryplusplus.core.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.dictionaryplusplus.data.local.dao.SeenEventDao
import com.example.dictionaryplusplus.data.local.dao.WordDao
import com.example.dictionaryplusplus.data.local.entity.SeenEventEntity
import com.example.dictionaryplusplus.data.local.entity.WordEntity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ConfirmSeenWordWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val seenEventDao: SeenEventDao,
    private val wordDao: WordDao
): CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val word = inputData.getString("EXTRA_WORD") ?: return Result.failure()
        return try {
            wordDao.insertWords(listOf(WordEntity(word)))
            seenEventDao.insertSeenEvent(
                SeenEventEntity(
                    word = word,
                    seenAtTimestamp = System.currentTimeMillis(),
                    isConfirmed = true
                )
            )
            Result.success()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Result.retry()
        }
    }
}