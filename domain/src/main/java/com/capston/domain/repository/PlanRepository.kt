package com.capston.domain.repository

import com.capston.domain.response.plan.GetPlanLectureRoomResponse
import com.capston.domain.request.PatchPlanDto
import com.capston.domain.request.PostNewPlanDto
import com.capston.domain.response.MessageResponse
import com.capston.domain.response.plan.GetPlanDetailResponse
import com.capston.domain.response.plan.LectureAliasResponse
import com.capston.domain.response.plan.PostPlanRescheduleResponse

interface PlanRepository {
    // 강의 생성
    suspend fun postNewPlan(
        postNewPlanDto: PostNewPlanDto
    ): MessageResponse

    // 강의 별명 수정
    suspend fun patchPlanName(
        planId: Int,
        patchPlanDto: PatchPlanDto
    ): LectureAliasResponse

    // 나의 강의실 조회
    suspend fun getPlanLectureRoom(): List<GetPlanLectureRoomResponse>

    // 상세 계획 조회
    suspend fun getPlanDetail(
        planId: Int
    ): GetPlanDetailResponse

    // 재스케줄링
    suspend fun postPlanReschedule(
        planId: Int
    ): PostPlanRescheduleResponse

    // 계획 삭제
    suspend fun deleteOnePlan(
        planId: Int
    ): MessageResponse
}