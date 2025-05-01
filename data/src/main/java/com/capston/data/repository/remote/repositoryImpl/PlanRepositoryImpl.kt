package com.capston.data.repository.remote.repositoryImpl

import com.capston.domain.datasource.PlanDataSource
import com.capston.domain.model.MyLecture
import com.capston.domain.repository.PlanRepository
import com.capston.domain.request.LoginDto
import com.capston.domain.request.PatchPlanDto
import com.capston.domain.request.PostPlanDto
import com.capston.domain.response.LoginResponse
import com.capston.domain.response.plan.GetPlanDetailResponse
import com.capston.domain.response.plan.LectureAliasResponse
import com.capston.domain.response.plan.PostPlanResponse
import javax.inject.Inject

class PlanRepositoryImpl @Inject constructor(
    private val planDataSource: PlanDataSource
) : PlanRepository {
    override suspend fun postPlanDetail(postPlanDto: PostPlanDto): PostPlanResponse =
        planDataSource.postPlanDetail(postPlanDto)

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