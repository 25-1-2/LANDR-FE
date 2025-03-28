package com.capston.domain.datasource

import com.capston.domain.request.PatchPlanDto
import com.capston.domain.response.BaseResponse
import kotlinx.coroutines.flow.Flow

interface PlanDataSource {
    // 강의 별명 수정
    suspend fun patchPlanName(
        planId: Int,
        patchPlanDto: PatchPlanDto
    ): String
}