package com.example.dictionaryplusplus.data.repository

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.dictionaryplusplus.core.worker.SyncUserScoreWorker
import com.example.dictionaryplusplus.domain.repository.ScoreSyncScheduler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScoreSyncSchedulerImpl @Inject constructor(
    private val workManager: WorkManager
): ScoreSyncScheduler {
    override suspend fun scheduleSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncUserScoreWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueue(syncRequest)
    }
}