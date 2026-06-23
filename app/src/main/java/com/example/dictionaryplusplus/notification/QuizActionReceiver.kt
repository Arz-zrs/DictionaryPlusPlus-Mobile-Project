package com.example.dictionaryplusplus.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.dictionaryplusplus.data.local.dao.SeenEventDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

class QuizActionReceiver : BroadcastReceiver() {
    @Inject
    lateinit var seenEventDao: SeenEventDao

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "ACTION_GOT_IT") {
            val eventId = intent.getLongExtra("EXTRA_SEEN_EVENT_ID", -1)
            if (eventId == -1L) return

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(888)

            CoroutineScope(Dispatchers.IO).launch {
                val matchingList = seenEventDao.getAllSeenEvents().firstOrNull() ?: emptyList()
                val targetEvent = matchingList.find { it.id == eventId }
                if (targetEvent != null) {
                    seenEventDao.insertSeenEvent(targetEvent.copy(isConfirmed = true))
                }
            }
        }
    }
}