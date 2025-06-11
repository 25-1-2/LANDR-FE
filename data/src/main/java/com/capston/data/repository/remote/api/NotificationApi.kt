package com.capston.data.repository.remote.api

import com.capston.domain.response.MessageResponse
import retrofit2.http.POST

interface NotificationApi {
    // 디데이 알림 테스트
    @POST("/v1/notifications/test/dday")
    suspend fun postDDayNotification(): MessageResponse

    // 미완료 강의 알림 테스트
    @POST("/v1/notifications/test/incomplete-lesson")
    suspend fun postIncompleteNotification(): MessageResponse
}