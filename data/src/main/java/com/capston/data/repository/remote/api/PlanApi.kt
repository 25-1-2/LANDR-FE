package com.capston.data.repository.remote.api

import com.capston.domain.request.PatchPlanDto
import retrofit2.http.PATCH
import retrofit2.http.Path

interface PlanApi {
    // 강의 별명 수정
    @PATCH("/v1/plans/{planId}/lecture-name")
    suspend fun patchPlanName(
        @Path("planId") planId: Int,
    ): PatchPlanDto
}