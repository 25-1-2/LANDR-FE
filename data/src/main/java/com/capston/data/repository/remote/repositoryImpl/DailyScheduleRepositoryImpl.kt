package com.capston.data.repository.remote.repositoryImpl

import com.capston.domain.datasource.DailyScheduleDataSource
import com.capston.domain.repository.DailyScheduleRepository
import com.capston.domain.response.home.DistinctHomeIdResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DailyScheduleRepositoryImpl @Inject constructor(
    private val dailyScheduleDataSource: DailyScheduleDataSource
) : DailyScheduleRepository{
    override suspend fun getDailySchedule(date: String): Flow<DistinctHomeIdResponse> {
        return dailyScheduleDataSource.getDailySchedule(date)
    }
}