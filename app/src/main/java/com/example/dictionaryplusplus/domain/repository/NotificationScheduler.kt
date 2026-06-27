package com.example.dictionaryplusplus.domain.repository

interface NotificationScheduler {
    fun scheduleWotdNotification(hour: Int, minute: Int)
    fun scheduleWotdApi(hour: Int, minute: Int)
    fun hasNotificationPermission(): Boolean
}