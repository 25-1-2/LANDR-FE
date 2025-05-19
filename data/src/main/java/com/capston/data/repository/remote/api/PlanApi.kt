package com.capston.data.repository.remote.api

import com.capston.domain.response.plan.GetPlanLectureRoomResponse
import com.capston.domain.request.PatchPlanDto
import com.capston.domain.request.PostNewPlanDto
import com.capston.domain.response.plan.DeleteOnePlanResponse
import com.capston.domain.response.plan.GetPlanDetailResponse
import com.capston.domain.response.plan.LectureAliasResponse
import com.capston.domain.response.plan.PostNewPlanResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface PlanApi {
    // 강의 생성
    @POST("/v1/plans")
    suspend fun postNewPlan(
        @Body postNewPlanDto: PostNewPlanDto
    ): PostNewPlanResponse

    // 강의 별명 수정
    @PATCH("/v1/plans/{planId}/lecture-name")
    suspend fun patchPlanName(
        @Path("planId") planId: Int,
        @Body patchPlanDto: PatchPlanDto
    ): LectureAliasResponse

    // 나의 강의실 조회
    @GET("/v1/plans/me")
    suspend fun getPlanLectureRoom(): List<GetPlanLectureRoomResponse>

    // 계획 상세 조회
    @GET("/v1/plans/{planId}")
    suspend fun getPlanDetail(
        @Path("planId") planId: Int
    ): GetPlanDetailResponse

    // 계획 상세 조회
    @DELETE("/v1/plans/{planId}")
    suspend fun deleteOnePlan(
        @Path("planId") planId: Int
    ): DeleteOnePlanResponse
}