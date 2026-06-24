package com.example.dictionaryplusplus.domain.repository

interface ScoreSyncScheduler {
    suspend fun scheduleSync()
}