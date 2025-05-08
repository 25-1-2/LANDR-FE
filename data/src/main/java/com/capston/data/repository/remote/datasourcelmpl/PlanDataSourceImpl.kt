package com.capston.data.repository.remote.datasourcelmpl

import com.capston.data.repository.remote.api.PlanApi
import com.capston.domain.datasource.PlanDataSource
import com.capston.domain.model.MyLecture
import com.capston.domain.request.PatchPlanDto
import com.capston.domain.request.PostNewPlanDto
import com.capston.domain.response.plan.GetPlanDetailResponse
import com.capston.domain.response.plan.LectureAliasResponse
import com.capston.domain.response.plan.PostNewPlanResponse
import javax.inject.Inject

class PlanDataSourceImpl @Inject constructor(
    private val planApi: PlanApi
) : PlanDataSource {
    override suspend fun postNewPlan(postNewPlanDto: PostNewPlanDto): PostNewPlanResponse {
        return planApi.postNewPlan(postNewPlanDto)
    }

    override suspend fun patchPlanName(planId: Int, patchPlanDto: PatchPlanDto): LectureAliasResponse {
        return planApi.patchPlanName(planId, patchPlanDto)
    }

    override suspend fun getPlanLectureRoom(): List<MyLecture> {
        return planApi.getPlanLectureRoom()
    }

    override suspend fun getPlanDetail(planId: Int): GetPlanDetailResponse {
        return planApi.getPlanDetail(planId)
    }
}