package com.capston.domain.repository

import com.capston.domain.request.PatchPlanDto
import com.capston.domain.response.BaseResponse
import kotlinx.coroutines.flow.Flow

interface PlanRepository {
    //강의 별명 수정
    suspend fun patchPlanName(
        planId: Int,
        patchPlanDto: PatchPlanDto
    ): Flow<BaseResponse<Any>>
}