package com.capston.domain.usecase

import com.capston.domain.repository.NotificationRepository
import com.capston.domain.response.MessageResponse
import javax.inject.Inject

class SendIncompleteNotificationUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(): Result<MessageResponse> {
        return notificationRepository.sendIncompleteNotification()
    }
}