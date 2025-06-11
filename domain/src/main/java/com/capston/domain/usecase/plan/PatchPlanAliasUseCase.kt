package com.capston.domain.usecase.plan

import com.capston.domain.repository.PlanRepository
import com.capston.domain.request.PatchPlanAliasDto
import com.capston.domain.response.plan.PatchPlanAliasResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PatchPlanAliasUseCase @Inject constructor(
    private val repository: PlanRepository
) {
    operator fun invoke(planId: Int, patchPlanAliasDto: PatchPlanAliasDto): Flow<PatchPlanAliasResponse> = flow {
        val response = repository.patchPlanAlias(planId, patchPlanAliasDto)
        emit(response)  // JSON 변환 없이 문자열 그대로 emit
    }
}