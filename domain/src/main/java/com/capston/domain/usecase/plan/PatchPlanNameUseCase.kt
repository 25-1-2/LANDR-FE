package com.capston.domain.usecase.plan

import com.capston.domain.repository.PlanRepository
import com.capston.domain.request.PatchPlanDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PatchPlanNameUseCase @Inject constructor(
    private val repository: PlanRepository
) {
    operator fun invoke(planId: Int, patchPlanDto: PatchPlanDto): Flow<String> = flow {
        val response = repository.patchPlanName(planId, patchPlanDto)
        emit(response)  // JSON 변환 없이 문자열 그대로 emit
    }
}
