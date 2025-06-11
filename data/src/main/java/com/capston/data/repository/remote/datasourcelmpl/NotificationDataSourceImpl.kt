package com.capston.domain.datasource

import com.capston.data.repository.remote.api.NotificationApi
import com.capston.data.repository.remote.datasource.NotificationDataSource
import com.capston.domain.response.MessageResponse
import javax.inject.Inject

class NotificationDataSourceImpl @Inject constructor(
    private val notificationApi: NotificationApi
) : NotificationDataSource {
    override suspend fun postDDayNotification(): MessageResponse {
        return notificationApi.postDDayNotification()
    }

    override suspend fun postIncompleteNotification(): MessageResponse {
        return notificationApi.postIncompleteNotification()
    }
}