package com.capston.domain.usecase.plan

import com.capston.domain.repository.PlanRepository
import com.capston.domain.request.PostNewPeriodPlanDto
import kotlinx.coroutines.flow.Flow
import com.capston.domain.response.MessageResponse
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PostNewPeriodPlanUseCase @Inject constructor(
    private val repository: PlanRepository
) {
    operator fun invoke(postNewPeriodPlanDto: PostNewPeriodPlanDto): Flow<MessageResponse> = flow {
        val response = repository.postNewPeriodPlan(postNewPeriodPlanDto)
        emit(response)
    }
}