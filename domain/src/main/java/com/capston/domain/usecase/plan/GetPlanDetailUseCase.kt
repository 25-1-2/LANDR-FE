package com.capston.domain.usecase.plan

import com.capston.domain.repository.PlanRepository
import com.capston.domain.response.plan.GetPlanDetailResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetPlanDetailUseCase @Inject constructor(
    private val repository: PlanRepository
) {
    operator fun invoke(planId: Int): Flow<GetPlanDetailResponse> = flow {
        val response = repository.getPlanDetail(planId)
        emit(response)  // JSON 변환 없이 문자열 그대로 emit
    }
}