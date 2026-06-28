package com.example.dictionaryplusplus.data.repository

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.dictionaryplusplus.BuildConfig
import com.example.dictionaryplusplus.core.worker.WotdNotificationWorker
import com.example.dictionaryplusplus.domain.repository.DebugRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DebugRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : DebugRepository {
    override fun triggerWotdWorker() {
        if (!BuildConfig.DEBUG) return

        val workRequest = OneTimeWorkRequestBuilder<WotdNotificationWorker>().build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }
}
