package com.capston.data.repository.remote.datasourcelmpl

import com.capston.domain.response.MessageResponse

interface NotificationRepository {
    suspend fun sendDDayNotification(): Result<MessageResponse>
    suspend fun sendIncompleteNotification(): Result<MessageResponse>
}