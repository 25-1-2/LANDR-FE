package com.capston.domain.usecase.plan

import com.capston.domain.repository.PlanRepository
import com.capston.domain.response.MessageResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PostPlanRescheduleUseCase @Inject constructor(
    private val repository: PlanRepository
) {
    operator fun invoke(planId: Int): Flow<MessageResponse> = flow {
        val response = repository.postPlanReschedule(planId)
        emit(response)
    }
}