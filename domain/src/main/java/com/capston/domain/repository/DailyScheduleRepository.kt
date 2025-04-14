package com.capston.domain.repository

import com.capston.domain.response.daily_schedule.DailyScheduleResponse
import kotlinx.coroutines.flow.Flow

interface DailyScheduleRepository {
    // 일일 스케줄 조회
    suspend fun getDailySchedule(
        date: String
    ): Flow<DailyScheduleResponse>
}