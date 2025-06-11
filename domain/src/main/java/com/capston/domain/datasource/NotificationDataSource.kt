package com.capston.data.repository.remote.api

import com.capston.domain.response.MessageResponse

interface NotificationDataSource {
    suspend fun postDDayNotification(): MessageResponse
    suspend fun postIncompleteNotification(): MessageResponse
}