package com.example.dictionaryplusplus.core.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.example.dictionaryplusplus.core.di.ApplicationScope
import com.example.dictionaryplusplus.data.local.dao.SeenEventDao
import com.example.dictionaryplusplus.data.local.dao.WordDao
import com.example.dictionaryplusplus.data.local.entity.SeenEventEntity
import com.example.dictionaryplusplus.data.local.entity.WordEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class QuizActionReceiver : BroadcastReceiver() {
    @Inject
    lateinit var seenEventDao: SeenEventDao

    @Inject
    lateinit var wordDao: WordDao

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    override fun onReceive(context: Context, intent: Intent) {
        val word = intent.getStringExtra("EXTRA_WORD") ?: return
        val shouldNavigate = intent.getBooleanExtra("EXTRA_NAVIGATE_TO_QUIZ", false)
        
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(888)

        if (shouldNavigate) {
            val quizUri = "dictionaryplusplus://quiz/$word".toUri()
            val quizIntent = Intent(Intent.ACTION_VIEW, quizUri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(quizIntent)
        }

        val pendingResult = goAsync()
        applicationScope.launch(Dispatchers.IO) {
            try {
                wordDao.insertWords(listOf(WordEntity(word)))
                seenEventDao.insertSeenEvent(
                    SeenEventEntity(
                        word = word,
                        seenAtTimestamp = System.currentTimeMillis(),
                        isConfirmed = true
                    )
                )
            } finally {
                pendingResult.finish()
            }
        }
    }
}
