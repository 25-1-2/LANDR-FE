package com.capston.domain.usecase.plan

import com.capston.domain.repository.PlanRepository
import com.capston.domain.request.PatchPlanDto
import com.capston.domain.response.BaseResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PatchPlanNameUseCase @Inject constructor(
    private val planRepository: PlanRepository
) {
    suspend operator fun invoke(
        planId: Int,
        patchPlanDto: PatchPlanDto
    ): Flow<String> = planRepository.patchPlanName(planId, patchPlanDto)
}