package com.capston.domain.datasource

import com.capston.domain.response.MessageResponse

interface NotificationDataSource {
    suspend fun postDDayNotification(): MessageResponse
    suspend fun postIncompleteNotification(): MessageResponse
}