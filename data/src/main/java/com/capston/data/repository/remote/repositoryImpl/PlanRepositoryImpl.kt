package com.capston.data.repository.remote.repositoryImpl

import com.capston.domain.datasource.PlanDataSource
import com.capston.domain.model.MyLecture
import com.capston.domain.repository.PlanRepository
import com.capston.domain.request.PatchPlanDto
import com.capston.domain.request.PostNewPlanDto
import com.capston.domain.response.plan.GetPlanDetailResponse
import com.capston.domain.response.plan.LectureAliasResponse
import com.capston.domain.response.plan.PostNewPlanResponse
import javax.inject.Inject

class PlanRepositoryImpl @Inject constructor(
    private val planDataSource: PlanDataSource
) : PlanRepository {
    override suspend fun postNewPlan(postNewPlanDto: PostNewPlanDto): PostNewPlanResponse =
        planDataSource.postNewPlan(postNewPlanDto)

    override suspend fun patchPlanName(
        planId: Int,
        patchPlanDto: PatchPlanDto
    ): LectureAliasResponse = planDataSource.patchPlanName(planId, patchPlanDto)

    override suspend fun getPlanLectureRoom(): List<MyLecture> =
        planDataSource.getPlanLectureRoom()

    override suspend fun getPlanDetail(
        planId: Int
    ): GetPlanDetailResponse = planDataSource.getPlanDetail(planId)
}