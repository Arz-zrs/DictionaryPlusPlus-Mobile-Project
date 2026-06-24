package com.example.dictionaryplusplus.data.repository

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.dictionaryplusplus.core.worker.DailyWordWorker
import com.example.dictionaryplusplus.core.worker.WotdApiWorker
import com.example.dictionaryplusplus.domain.repository.NotificationScheduler
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationSchedulerImpl @Inject constructor(
    private val workManager: WorkManager
): NotificationScheduler {
    override fun scheduleDailyWord(hour: Int, minute: Int) {
        val initialDelay = computeInitialDelayMillis(hour, minute)
        val request = PeriodicWorkRequestBuilder<DailyWordWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(false)
                    .setRequiresCharging(false)
                    .build()
            )
            .build()
        workManager.enqueueUniquePeriodicWork(
            "daily_word_worker",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    override fun scheduleWotd() {
        val request = PeriodicWorkRequestBuilder<WotdApiWorker>(24, TimeUnit.HOURS)
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()
        workManager.enqueueUniquePeriodicWork(
            "wotd_api_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    private fun computeInitialDelayMillis(hour: Int, minute: Int): Long {
        val now = LocalDateTime.now()
        var target = now.toLocalDate().atTime(hour, minute)
        if (target.isBefore(now)) target = target.plusDays(1)
        return Duration.between(now, target).toMillis()
    }
}