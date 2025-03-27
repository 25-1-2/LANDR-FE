package com.capston.data.repository.remote.repositoryImpl

import com.capston.domain.datasource.PlanDataSource
import com.capston.domain.repository.PlanRepository
import com.capston.domain.request.PatchPlanDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlanRepositoryImpl @Inject constructor(
    private val planDataSource: PlanDataSource
) : PlanRepository {
    override suspend fun patchPlanName(
        planId: Int
    ): Flow<PatchPlanDto> = planDataSource.patchPlanName(planId)
}