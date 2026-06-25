package com.example.dictionaryplusplus.data.repository

import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.dictionaryplusplus.core.worker.DailyWordWorker
import com.example.dictionaryplusplus.core.worker.WotdApiWorker
import com.example.dictionaryplusplus.data.local.UserPreferences
import com.example.dictionaryplusplus.domain.repository.DebugRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DebugRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreferences: UserPreferences
) : DebugRepository {

    override fun triggerWotdWorker() {
        val workRequest = OneTimeWorkRequestBuilder<WotdApiWorker>().build()
        WorkManager.getInstance(context).enqueue(workRequest)
        Log.d("MOBIL", "Word of the day worker triggered")
    }

    override fun triggerDailyWordWorker() {
        val workRequest = OneTimeWorkRequestBuilder<DailyWordWorker>().build()
        WorkManager.getInstance(context).enqueue(workRequest)
        Log.d("MOBIL", "Daily word worker triggered")
    }

    override suspend fun resetQuizCompletion() {
        userPreferences.resetQuizCompletion()
    }
}
