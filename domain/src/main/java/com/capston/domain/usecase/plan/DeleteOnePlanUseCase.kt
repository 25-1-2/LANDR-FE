package com.capston.domain.usecase.plan

import com.capston.domain.repository.PlanRepository
import com.capston.domain.response.plan.DeleteOnePlanResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteOnePlanUseCase @Inject constructor(
    private val repository: PlanRepository
) {
    operator fun invoke(planId: Int): Flow<DeleteOnePlanResponse> = flow {
        val response = repository.deleteOnePlan(planId)
        emit(response)  // JSON 변환 없이 문자열 그대로 emit
    }
}