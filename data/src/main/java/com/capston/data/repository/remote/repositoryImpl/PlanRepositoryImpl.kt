package com.capston.data.repository.remote.repositoryImpl

import com.capston.domain.datasource.PlanDataSource
import com.capston.domain.response.plan.GetPlanLectureRoomResponse
import com.capston.domain.repository.PlanRepository
import com.capston.domain.request.PatchPeriodPlanDto
import com.capston.domain.request.PatchPlanAliasDto
import com.capston.domain.request.PatchTimePlanDto
import com.capston.domain.request.PostNewPlanDto
import com.capston.domain.response.MessageResponse
import com.capston.domain.response.plan.PlanDetailResponse
import com.capston.domain.response.plan.PatchPlanAliasResponse
import javax.inject.Inject

class PlanRepositoryImpl @Inject constructor(
    private val planDataSource: PlanDataSource
) : PlanRepository {
    override suspend fun postNewPlan(postNewPlanDto: PostNewPlanDto): MessageResponse =
        planDataSource.postNewPlan(postNewPlanDto)

    override suspend fun patchPlanAlias(
        planId: Int,
        patchPlanAliasDto: PatchPlanAliasDto
    ): PatchPlanAliasResponse = planDataSource.patchPlanAlias(planId, patchPlanAliasDto)

    override suspend fun getPlanLectureRoom(): List<GetPlanLectureRoomResponse> =
        planDataSource.getPlanLectureRoom()

    override suspend fun getPlanDetail(
        planId: Int
    ): PlanDetailResponse = planDataSource.getPlanDetail(planId)

    override suspend fun postPlanReschedule(
        planId: Int
    ): MessageResponse = planDataSource.postPlanReschedule(planId)

    override suspend fun patchPeriodPlan(
        planId: Int,
        patchPeriodPlanDto: PatchPeriodPlanDto
    ): MessageResponse = planDataSource.patchPeriodPlan(planId, patchPeriodPlanDto)

    override suspend fun patchTimePlan(
        planId: Int,
        patchTimePlanDto: PatchTimePlanDto
    ): MessageResponse = planDataSource.patchTimePlan(planId, patchTimePlanDto)

    override suspend fun deleteOnePlan(
        planId: Int
    ): MessageResponse = planDataSource.deleteOnePlan(planId)
}