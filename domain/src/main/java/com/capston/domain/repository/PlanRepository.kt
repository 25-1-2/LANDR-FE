package com.capston.domain.repository

import com.capston.domain.request.PatchPlanDto
import com.capston.domain.response.plan.LectureAliasResponse

interface PlanRepository {
    //강의 별명 수정
    suspend fun patchPlanName(
        planId: Int,
        patchPlanDto: PatchPlanDto
    ): LectureAliasResponse
}