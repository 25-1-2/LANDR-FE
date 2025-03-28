package com.capston.data.repository.remote.datasourcelmpl

import android.util.Log
import com.capston.data.repository.remote.api.PlanApi
import com.capston.domain.datasource.PlanDataSource
import com.capston.domain.request.PatchPlanDto
import javax.inject.Inject

class PlanDataSourceImpl @Inject constructor(
    private val planApi: PlanApi
) : PlanDataSource {

    override suspend fun patchPlanName(planId: Int, patchPlanDto: PatchPlanDto): String {
        return try {
            val response = planApi.patchPlanName(planId, patchPlanDto)
            response.trim() // 🔥 `ResponseBody` -> `String` 변환 후 공백 제거
        } catch (e: Exception) {
            Log.e("PlanDataSourceImpl", "PATCH 요청 실패: ${e.message}")
            throw e  // 예외 다시 던지기 (ViewModel에서 처리 가능)
        }
    }
}
