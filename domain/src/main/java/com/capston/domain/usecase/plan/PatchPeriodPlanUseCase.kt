package com.capston.domain.usecase.plan

import com.capston.domain.repository.PlanRepository
import com.capston.domain.request.PatchPeriodPlanDto
import com.capston.domain.response.MessageResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PatchPeriodPlanUseCase @Inject constructor(
    private val repository: PlanRepository
) {
    operator fun invoke(planId: Int, patchPeriodPlanDto: PatchPeriodPlanDto): Flow<MessageResponse> = flow {
        val response = repository.patchPeriodPlan(planId, patchPeriodPlanDto)
        emit(response)  // JSON 변환 없이 문자열 그대로 emit
    }
}