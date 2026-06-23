package com.example.dictionaryplusplus.core.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.dictionaryplusplus.core.di.ApplicationScope
import com.example.dictionaryplusplus.data.local.dao.SeenEventDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class QuizActionReceiver : BroadcastReceiver() {
    @Inject
    lateinit var seenEventDao: SeenEventDao

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "ACTION_GOT_IT") {
            val eventId = intent.getLongExtra("EXTRA_SEEN_EVENT_ID", -1)
            if (eventId == -1L) return

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(888)

            val pendingResult = goAsync()
            applicationScope.launch(Dispatchers.IO) {
                try {
                    val matchingList = seenEventDao.getAllSeenEvents().firstOrNull() ?: emptyList()
                    val targetEvent = matchingList.find { it.id == eventId }
                    if (targetEvent != null) {
                        seenEventDao.confirmSeenEvent(eventId)
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
