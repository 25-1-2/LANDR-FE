package com.capston.data.repository.remote.repositoryImpl

import com.capston.domain.datasource.HomeDataSource
import com.capston.domain.repository.HomeRepository
import com.capston.domain.request.UpdateDDayRequest
import com.capston.domain.response.CheckResponse
import com.capston.domain.response.home.DDayResponse
import com.capston.domain.response.home.DistinctHomeIdResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val homeDataSource: HomeDataSource
) : HomeRepository {

    override suspend fun getDistinctHome(): Flow<DistinctHomeIdResponse> {
        return homeDataSource.getDistinctHome()
    }

    override suspend fun patchLessonSchedulesCheckToggle(lessonScheduleId: Int): Flow<CheckResponse> {
        return homeDataSource.patchLessonSchedulesCheckToggle(lessonScheduleId)
    }

    override suspend fun postDDay(updateDDayRequest: UpdateDDayRequest): Flow<DDayResponse> {
        return homeDataSource.postDDay(updateDDayRequest)
    }

    override suspend fun getDDay(dDayId: Int): Flow<DDayResponse> {
        return homeDataSource.getDDay(dDayId)
    }

    override suspend fun deleteDDay(dDayId: Int) {
        return homeDataSource.deleteDDay(dDayId)
    }

    override suspend fun patchDDay(
        dDayId: Int,
        updateDDayRequest: UpdateDDayRequest
    ): Flow<DDayResponse> {
        return homeDataSource.patchDDay(dDayId, updateDDayRequest)
    }
}
