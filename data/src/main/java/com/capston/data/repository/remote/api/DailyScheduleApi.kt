package com.capston.data.repository.remote.api

import com.capston.domain.response.daily_schedule.DailyScheduleResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DailyScheduleApi {
    //일일 스케줄 조회
    @GET("/v1/daily-schedules")
    suspend fun getDailySchedule(@Query("date") date: String
    ): DailyScheduleResponse
}
