package com.capston.domain.repository

import com.capston.domain.request.PatchPlanDto
import com.capston.domain.request.PostPlanDto
import com.capston.domain.response.plan.LectureAliasResponse
import com.capston.domain.response.plan.PostPlanResponse

interface PlanRepository {
    //강의 별명 수정
    suspend fun postPlanDetail(
        postPlanDto: PostPlanDto
    ): PostPlanResponse

    //강의 별명 수정
    suspend fun patchPlanName(
        planId: Int,
        patchPlanDto: PatchPlanDto
    ): LectureAliasResponse
}