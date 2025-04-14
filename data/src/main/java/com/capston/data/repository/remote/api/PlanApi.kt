package com.capston.data.repository.remote.api

import com.capston.domain.request.PatchPlanDto
import com.capston.domain.request.PostPlanDto
import com.capston.domain.response.plan.LectureAliasResponse
import com.capston.domain.response.plan.PostPlanResponse
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface PlanApi {
    // 강의 생성
    @POST("/v1/plans")
    suspend fun postPlanDetail(
        @Body postPlanDto: PostPlanDto
    ): PostPlanResponse

    // 강의 별명 수정
    @PATCH("/v1/plans/{planId}/lecture-name")
    suspend fun patchPlanName(
        @Path("planId") planId: Int,
        @Body patchPlanDto: PatchPlanDto
    ): LectureAliasResponse
}