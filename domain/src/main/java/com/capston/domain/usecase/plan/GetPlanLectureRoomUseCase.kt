package com.capston.domain.usecase.plan

import com.capston.domain.repository.PlanRepository
import com.capston.domain.response.plan.GetPlanLectureRoomResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetPlanLectureRoomUseCase @Inject constructor(
    private val repository: PlanRepository
) {
    operator fun invoke(): Flow<GetPlanLectureRoomResponse> = flow {
        val response = repository.getPlanLectureRoom()
        emit(response)  // JSON 변환 없이 문자열 그대로 emit
    }
}