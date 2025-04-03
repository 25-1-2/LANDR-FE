package com.capston.domain.usecase.daily_schedule

import com.capston.domain.base.Result
import com.capston.domain.repository.DailyScheduleRepository
import com.capston.domain.repository.ErrorRepository
import com.capston.domain.response.home.DistinctHomeIdResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDailyScheduleUseCase @Inject constructor(
    private val dailyScheduleRepository: DailyScheduleRepository
){
    suspend operator fun invoke(
        date: String
    ): Flow<DistinctHomeIdResponse> {
        return dailyScheduleRepository.getDailySchedule(date)
    }
}