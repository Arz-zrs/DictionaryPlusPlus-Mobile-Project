package com.example.dictionaryplusplus.core.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.dictionaryplusplus.core.worker.ConfirmSeenWordWorker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuizActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val word = intent.getStringExtra("EXTRA_WORD") ?: return
        
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(888)

        val workRequest = OneTimeWorkRequestBuilder<ConfirmSeenWordWorker>()
            .setInputData(workDataOf("EXTRA_WORD" to word))
            .build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }
}
