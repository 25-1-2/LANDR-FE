package com.capston.data.repository.remote.api

import com.capston.domain.request.PatchPeriodPlanDto
import com.capston.domain.response.plan.GetPlanLectureRoomResponse
import com.capston.domain.request.PatchPlanAliasDto
import com.capston.domain.request.PatchTimePlanDto
import com.capston.domain.request.PostNewPlanDto
import com.capston.domain.response.MessageResponse
import com.capston.domain.response.plan.PlanDetailResponse
import com.capston.domain.response.plan.PatchPlanAliasResponse
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
    ): MessageResponse

    // 강의 별명 수정
    @PATCH("/v1/plans/{planId}/lecture-name")
    suspend fun patchPlanAlias(
        @Path("planId") planId: Int,
        @Body patchPlanAliasDto: PatchPlanAliasDto
    ): PatchPlanAliasResponse

    // 나의 강의실 조회
    @GET("/v1/plans/me")
    suspend fun getPlanLectureRoom(): List<GetPlanLectureRoomResponse>

    // 계획 상세 조회
    @GET("/v1/plans/{planId}")
    suspend fun getPlanDetail(
        @Path("planId") planId: Int
    ): PlanDetailResponse

    // 재스케줄링
    @POST("/v1/plans/{planId}/reschedule")
    suspend fun postPlanReschedule(
        @Path("planId") planId: Int
    ): MessageResponse

    // 기간 계획 수정
    @PATCH("/v1/plans/{planId}")
    suspend fun patchPeriodPlan(
        @Path("planId") planId: Int,
        @Body patchPeriodPlanDto: PatchPeriodPlanDto
    ): MessageResponse

    // 시간 계획 수정
    @PATCH("/v1/plans/{planId}")
    suspend fun patchTimePlan(
        @Path("planId") planId: Int,
        @Body patchTimePlanDto: PatchTimePlanDto
    ): MessageResponse

    // 계획 삭제
    @DELETE("/v1/plans/{planId}")
    suspend fun deleteOnePlan(
        @Path("planId") planId: Int
    ): MessageResponse
}