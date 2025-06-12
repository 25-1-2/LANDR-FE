package com.capston.domain.repository

import com.capston.domain.request.PatchPeriodPlanDto
import com.capston.domain.response.plan.GetPlanLectureRoomResponse
import com.capston.domain.request.PatchPlanAliasDto
import com.capston.domain.request.PatchTimePlanDto
import com.capston.domain.request.PostNewPeriodPlanDto
import com.capston.domain.request.PostNewTimePlanDto
import com.capston.domain.response.MessageResponse
import com.capston.domain.response.plan.PlanDetailResponse
import com.capston.domain.response.plan.PatchPlanAliasResponse

interface PlanRepository {
    // 기간으로 강의 생성
    suspend fun postNewPeriodPlan(
        postNewPeriodPlanDto: PostNewPeriodPlanDto
    ): MessageResponse

    // 기간으로 강의 생성
    suspend fun postNewTimePlan(
        postNewTimePlanDto: PostNewTimePlanDto
    ): MessageResponse

    // 강의 별명 수정
    suspend fun patchPlanAlias(
        planId: Int,
        patchPlanAliasDto: PatchPlanAliasDto
    ): PatchPlanAliasResponse

    // 나의 강의실 조회
    suspend fun getPlanLectureRoom(): List<GetPlanLectureRoomResponse>

    // 상세 계획 조회
    suspend fun getPlanDetail(
        planId: Int
    ): PlanDetailResponse

    // 재스케줄링
    suspend fun postPlanReschedule(
        planId: Int
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
        planId: Int
    ): MessageResponse
}