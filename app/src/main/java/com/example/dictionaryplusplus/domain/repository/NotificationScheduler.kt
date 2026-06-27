package com.example.dictionaryplusplus.domain.repository

interface NotificationScheduler {
    fun scheduleDailyWord(hour: Int, minute: Int)
    fun scheduleWotd(hour: Int, minute: Int)
    fun hasNotificationPermission(): Boolean
}