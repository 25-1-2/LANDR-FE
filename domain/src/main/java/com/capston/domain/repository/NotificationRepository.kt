package com.capston.domain.repository

import com.capston.domain.response.MessageResponse

interface NotificationRepository {
    suspend fun sendDDayNotification(): Result<MessageResponse>
    suspend fun sendIncompleteNotification(): Result<MessageResponse>
}