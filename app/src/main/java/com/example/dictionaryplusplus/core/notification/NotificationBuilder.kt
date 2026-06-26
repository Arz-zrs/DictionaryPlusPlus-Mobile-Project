package com.example.dictionaryplusplus.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.dictionaryplusplus.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

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
        word: String,
        phonetic: String,
        shortDefinition: String
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val pendingIntentFlags =
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        val actionIntent = Intent(context, QuizActionReceiver::class.java).apply {
            putExtra("EXTRA_WORD", word)
        }
        
        val actionPendingIntent = PendingIntent.getBroadcast(
            context, 100, actionIntent, pendingIntentFlags
        )

        // Content intent (main tap) should open an Activity to satisfy lint/best practices
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("EXTRA_WORD_TO_SAVE", word)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context, 0, contentIntent, pendingIntentFlags
        )

        val quizMeIntent = Intent(context, QuizActionReceiver::class.java).apply {
            putExtra("EXTRA_WORD", word)
            putExtra("EXTRA_NAVIGATE_TO_QUIZ", true)
        }
        val quizMePendingIntent = PendingIntent.getBroadcast(
            context, 200, quizMeIntent, pendingIntentFlags
        )

        val wotdTitle = "Word of the Day: $word"
        val wotdText: String =
            if (!phonetic.isBlank())  "$phonetic - Tap to save to history"
            else "Tap to save to history"
        val wotdTextLong = "$word $phonetic\n\nDefinition:\n$shortDefinition"

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(wotdTitle)
            .setContentText(wotdText)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(wotdTextLong)
            )
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .addAction(0, "Got It", actionPendingIntent)
            .addAction(0, "Quiz Me", quizMePendingIntent)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}