package com.capston.domain.usecase.daily_schedule

import com.capston.domain.repository.DailyScheduleRepository
import com.capston.domain.response.daily_schedule.DailyScheduleResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDailyScheduleUseCase @Inject constructor(
    private val dailyScheduleRepository: DailyScheduleRepository
){
    suspend operator fun invoke(
        date: String
    ): Flow<DailyScheduleResponse> {
        return dailyScheduleRepository.getDailySchedule(date)
    }
}