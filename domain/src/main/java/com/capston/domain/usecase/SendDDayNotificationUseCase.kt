package com.capston.data.repository.remote.repositoryImpl

import com.capston.domain.repository.NotificationRepository
import com.capston.domain.response.MessageResponse
import javax.inject.Inject

class SendDDayNotificationUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(): Result<MessageResponse> {
        return notificationRepository.sendDDayNotification()
    }
}