package com.capston.data.repository.remote.repositoryImpl

import com.capston.domain.datasource.NotificationDataSource
import com.capston.domain.repository.NotificationRepository
import com.capston.domain.response.MessageResponse
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationDataSource: NotificationDataSource
) : NotificationRepository {
    override suspend fun sendDDayNotification(): Result<MessageResponse> {
        return try {
            val response = notificationDataSource.postDDayNotification()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendIncompleteNotification(): Result<MessageResponse> {
        return try {
            val response = notificationDataSource.postIncompleteNotification()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}