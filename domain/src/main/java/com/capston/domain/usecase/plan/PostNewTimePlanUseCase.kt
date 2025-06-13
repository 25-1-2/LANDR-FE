package com.capston.domain.usecase.plan

import com.capston.domain.repository.PlanRepository
import com.capston.domain.request.PostNewPeriodPlanDto
import com.capston.domain.request.PostNewTimePlanDto
import kotlinx.coroutines.flow.Flow
import com.capston.domain.response.MessageResponse
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PostNewTimePlanUseCase @Inject constructor(
    private val repository: PlanRepository
) {
    operator fun invoke(postNewTimePlanDto: PostNewTimePlanDto): Flow<MessageResponse> = flow {
        val response = repository.postNewTimePlan(postNewTimePlanDto)
        emit(response)
    }
}