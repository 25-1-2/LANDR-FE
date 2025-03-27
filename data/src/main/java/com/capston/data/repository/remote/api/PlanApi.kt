package com.capston.data.repository.remote.api

import com.capston.domain.request.PatchPlanDto
import com.capston.domain.response.BaseResponse
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.Path

interface PlanApi {
    // 강의 별명 수정
    @PATCH("/v1/plans/{planId}/lecture-name")
    suspend fun patchPlanName(
        @Path("planId") planId: Int,
        @Body patchPlanDto: PatchPlanDto
    ): String
}