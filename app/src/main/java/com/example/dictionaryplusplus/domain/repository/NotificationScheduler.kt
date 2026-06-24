package com.example.dictionaryplusplus.domain.repository

interface NotificationScheduler {
    fun scheduleDailyWord(hour: Int, minute: Int)
    fun scheduleWotd()
}