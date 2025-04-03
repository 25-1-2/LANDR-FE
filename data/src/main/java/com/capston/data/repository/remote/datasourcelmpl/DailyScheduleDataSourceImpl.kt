package com.capston.data.repository.remote.datasourcelmpl

import com.capston.data.repository.remote.api.DailyScheduleApi
import com.capston.data.repository.remote.api.PlanApi
import com.capston.domain.datasource.DailyScheduleDataSource
import com.capston.domain.datasource.PlanDataSource
import com.capston.domain.request.PatchPlanDto
import com.capston.domain.response.home.DistinctHomeIdResponse
import com.capston.domain.response.plan.LectureAliasResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DailyScheduleDataSourceImpl @Inject constructor(
    private val dailyScheduleApi: DailyScheduleApi
) : DailyScheduleDataSource{
    override suspend fun getDailySchedule(date: String): Flow<DistinctHomeIdResponse> {
        return dailyScheduleApi.getDailySchedule(date)
    }
}