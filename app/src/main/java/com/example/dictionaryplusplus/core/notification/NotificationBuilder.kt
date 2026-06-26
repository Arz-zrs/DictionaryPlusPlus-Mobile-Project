package com.example.dictionaryplusplus.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.net.toUri

@Singleton
class NotificationBuilder @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val channelId = "daily_vocabulary_channel"
    private val notificationId = 888

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Daily Vocabulary"
            val descriptionText = "Daily vocabulary notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showDailyNotification(
        eventId: Long,
        word: String,
        phonetic: String,
        shortDefinition: String
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val pendingIntentFlags =
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        val gotItIntent = Intent(context, QuizActionReceiver::class.java).apply {
            action = "ACTION_GOT_IT"
            putExtra("EXTRA_SEEN_EVENT_ID", eventId)
        }
        val gotItPendingIntent = PendingIntent.getBroadcast(
            context, 100, gotItIntent, pendingIntentFlags
        )

        val quizUri = "dictionaryplusplus://quiz/$word".toUri()
        val quizIntent = Intent(Intent.ACTION_VIEW, quizUri)
        val quizPendingIntent = PendingIntent.getActivity(
            context, 200, quizIntent, pendingIntentFlags
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("Word of the Day: $word")
            .setContentText("$phonetic - Tap to learn more")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$word $phonetic\n\nDefinition:\n$shortDefinition")
            )
            .setAutoCancel(true)
            .setContentIntent(quizPendingIntent)
            .addAction(0, "Got It", gotItPendingIntent)
            .addAction(0, "Quiz Me", quizPendingIntent)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}