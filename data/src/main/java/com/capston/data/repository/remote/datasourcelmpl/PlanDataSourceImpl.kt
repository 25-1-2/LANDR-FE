package com.capston.data.repository.remote.datasourcelmpl

import android.util.Log
import com.capston.data.repository.remote.api.PlanApi
import com.capston.domain.datasource.PlanDataSource
import com.capston.domain.request.PatchPlanDto
import com.capston.domain.response.BaseResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PlanDataSourceImpl @Inject constructor(
    private val planApi: PlanApi
) : PlanDataSource  {
    override suspend fun patchPlanName(
        planId: Int,
        patchPlanDto: PatchPlanDto
    ): Flow<BaseResponse<Any>> = flow {
        val result = planApi.patchPlanName(planId, patchPlanDto)
        emit(result)
    }.catch { e ->
        Log.e("patchPlanEdit 에러", e.message.toString())
    }
}