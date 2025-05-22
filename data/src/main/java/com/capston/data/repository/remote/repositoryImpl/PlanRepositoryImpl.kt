package com.capston.data.repository.remote.repositoryImpl

import com.capston.domain.datasource.PlanDataSource
import com.capston.domain.response.plan.GetPlanLectureRoomResponse
import com.capston.domain.repository.PlanRepository
import com.capston.domain.request.PatchPlanDto
import com.capston.domain.request.PostNewPlanDto
import com.capston.domain.response.MessageResponse
import com.capston.domain.response.plan.GetPlanDetailResponse
import com.capston.domain.response.plan.LectureAliasResponse
import com.capston.domain.response.plan.PostNewPlanResponse
import com.capston.domain.response.plan.PostPlanRescheduleResponse
import javax.inject.Inject

class PlanRepositoryImpl @Inject constructor(
    private val planDataSource: PlanDataSource
) : PlanRepository {
    override suspend fun postNewPlan(postNewPlanDto: PostNewPlanDto): MessageResponse =
        planDataSource.postNewPlan(postNewPlanDto)

    override suspend fun patchPlanName(
        planId: Int,
        patchPlanDto: PatchPlanDto
    ): LectureAliasResponse = planDataSource.patchPlanName(planId, patchPlanDto)

    override suspend fun getPlanLectureRoom(): List<GetPlanLectureRoomResponse> =
        planDataSource.getPlanLectureRoom()

    override suspend fun getPlanDetail(
        planId: Int
    ): GetPlanDetailResponse = planDataSource.getPlanDetail(planId)

    override suspend fun postPlanReschedule(
        planId: Int
    ): PostPlanRescheduleResponse = planDataSource.postPlanReschedule(planId)

    override suspend fun deleteOnePlan(
        planId: Int
    ): MessageResponse = planDataSource.deleteOnePlan(planId)
}