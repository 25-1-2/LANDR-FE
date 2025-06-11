package com.capston.data.repository.remote.datasourcelmpl

import com.capston.data.repository.remote.api.PlanApi
import com.capston.domain.datasource.PlanDataSource
import com.capston.domain.request.PatchPeriodPlanDto
import com.capston.domain.response.plan.GetPlanLectureRoomResponse
import com.capston.domain.request.PatchPlanAliasDto
import com.capston.domain.request.PatchTimePlanDto
import com.capston.domain.request.PostNewPlanDto
import com.capston.domain.response.MessageResponse
import com.capston.domain.response.plan.PlanDetailResponse
import com.capston.domain.response.plan.PatchPlanAliasResponse
import javax.inject.Inject

class PlanDataSourceImpl @Inject constructor(
    private val planApi: PlanApi
) : PlanDataSource {
    override suspend fun postNewPlan(postNewPlanDto: PostNewPlanDto): MessageResponse {
        return planApi.postNewPlan(postNewPlanDto)
    }

    override suspend fun patchPlanAlias(planId: Int, patchPlanAliasDto: PatchPlanAliasDto): PatchPlanAliasResponse {
        return planApi.patchPlanAlias(planId, patchPlanAliasDto)
    }

    override suspend fun getPlanLectureRoom(): List<GetPlanLectureRoomResponse> {
        return planApi.getPlanLectureRoom()
    }

    override suspend fun getPlanDetail(planId: Int): PlanDetailResponse {
        return planApi.getPlanDetail(planId)
    }

    override suspend fun postPlanReschedule(planId: Int): MessageResponse {
        return planApi.postPlanReschedule(planId)
    }

    override suspend fun patchPeriodPlan(
        planId: Int,
        patchPeriodPlanDto: PatchPeriodPlanDto
    ): MessageResponse {
        return planApi.patchPeriodPlan(planId, patchPeriodPlanDto)
    }

    override suspend fun patchTimePlan(
        planId: Int,
        patchTimePlanDto: PatchTimePlanDto
    ): MessageResponse {
        return planApi.patchTimePlan(planId, patchTimePlanDto)
    }

    override suspend fun deleteOnePlan(planId: Int): MessageResponse {
        return planApi.deleteOnePlan(planId)
    }
}