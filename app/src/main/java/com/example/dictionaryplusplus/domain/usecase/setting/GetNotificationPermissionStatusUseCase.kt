package com.example.dictionaryplusplus.domain.usecase.setting

import com.example.dictionaryplusplus.domain.repository.NotificationScheduler
import javax.inject.Inject

class GetNotificationPermissionStatusUseCase @Inject constructor(
    private val notificationScheduler: NotificationScheduler
) {
    operator fun invoke(): Boolean {
        return notificationScheduler.hasNotificationPermission()
    }
}
