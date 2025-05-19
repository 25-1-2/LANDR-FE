package com.capston.domain.datasource

import com.capston.domain.response.plan.GetPlanLectureRoomResponse
import com.capston.domain.request.PatchPlanDto
import com.capston.domain.request.PostNewPlanDto
import com.capston.domain.response.plan.GetPlanDetailResponse
import com.capston.domain.response.plan.LectureAliasResponse
import com.capston.domain.response.plan.PostNewPlanResponse

interface PlanDataSource {
    // 강의 생성
    suspend fun postNewPlan(
        postNewPlanDto: PostNewPlanDto
    ): PostNewPlanResponse

    // 강의 별명 수정
    suspend fun patchPlanName(
        planId: Int,
        patchPlanDto: PatchPlanDto
    ): LectureAliasResponse

    // 나의 강의실 조회
    suspend fun getPlanLectureRoom(): List<GetPlanLectureRoomResponse>

    // 강의 상세 조회
    suspend fun getPlanDetail(
        planId: Int,
    ): GetPlanDetailResponse
}