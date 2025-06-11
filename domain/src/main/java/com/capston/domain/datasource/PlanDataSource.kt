package com.capston.domain.datasource

import com.capston.domain.request.PatchPeriodPlanDto
import com.capston.domain.response.plan.GetPlanLectureRoomResponse
import com.capston.domain.request.PatchPlanAliasDto
import com.capston.domain.request.PatchTimePlanDto
import com.capston.domain.request.PostNewPlanDto
import com.capston.domain.response.MessageResponse
import com.capston.domain.response.plan.PlanDetailResponse
import com.capston.domain.response.plan.PatchPlanAliasResponse

interface PlanDataSource {
    // 강의 생성
    suspend fun postNewPlan(
        postNewPlanDto: PostNewPlanDto
    ): MessageResponse

    // 강의 별명 수정
    suspend fun patchPlanAlias(
        planId: Int,
        patchPlanAliasDto: PatchPlanAliasDto
    ): PatchPlanAliasResponse

    // 나의 강의실 조회
    suspend fun getPlanLectureRoom(): List<GetPlanLectureRoomResponse>

    // 강의 상세 조회
    suspend fun getPlanDetail(
        planId: Int,
    ): PlanDetailResponse

    // 재스케줄링
    suspend fun postPlanReschedule(
        planId: Int,
    ): MessageResponse

    suspend fun patchPeriodPlan(
        planId: Int,
        patchPeriodPlanDto: PatchPeriodPlanDto
    ): MessageResponse

    suspend fun patchTimePlan(
        planId: Int,
        patchTimePlanDto: PatchTimePlanDto
    ): MessageResponse

    // 계획 삭제
    suspend fun deleteOnePlan(
        planId: Int,
    ): MessageResponse
}