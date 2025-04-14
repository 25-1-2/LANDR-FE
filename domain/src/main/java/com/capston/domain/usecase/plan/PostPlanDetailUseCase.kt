package com.capston.domain.usecase.plan

import com.capston.domain.repository.PlanRepository
import com.capston.domain.request.PatchPlanDto
import com.capston.domain.request.PostPlanDto
import com.capston.domain.response.plan.LectureAliasResponse
import com.capston.domain.response.plan.PostPlanResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PostPlanDetailUseCase @Inject constructor(
    private val repository: PlanRepository
) {
    operator fun invoke(postPlanDto: PostPlanDto): Flow<PostPlanResponse> = flow {
        val response = repository.postPlanDetail(postPlanDto)
        emit(response)
    }
}