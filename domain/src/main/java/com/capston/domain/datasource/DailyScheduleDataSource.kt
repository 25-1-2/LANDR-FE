package com.capston.domain.datasource

import com.capston.domain.base.Result
import com.capston.domain.response.home.DistinctHomeIdResponse
import kotlinx.coroutines.flow.Flow

interface DailyScheduleDataSource {
    // 일일 스케줄 조회
    suspend fun getDailySchedule(date: String): Flow<DistinctHomeIdResponse>
}