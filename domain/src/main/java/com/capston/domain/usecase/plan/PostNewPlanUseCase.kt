package com.capston.domain.usecase.plan

import com.capston.domain.repository.PlanRepository
import com.capston.domain.request.PostNewPlanDto
import com.capston.domain.response.plan.PostNewPlanResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PostNewPlanUseCase @Inject constructor(
    private val repository: PlanRepository
) {
    operator fun invoke(postNewPlanDto: PostNewPlanDto): Flow<PostNewPlanResponse> = flow {
        val response = repository.postNewPlan(postNewPlanDto)
        emit(response)
    }
}