package com.capston.data.repository.remote.api

import com.capston.domain.base.Result
import com.capston.domain.response.home.DistinctHomeIdResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET

interface DailyScheduleApi {
    //일일 스케줄 조회
    @GET("/v1/daily-schedules")
    suspend fun getDailySchedule(date: String
    ): Flow<DistinctHomeIdResponse>
}
