package com.capston.domain.usecase.recommend

import com.capston.domain.repository.PlanRepository
import com.capston.domain.repository.RecommendRepository
import com.capston.domain.request.RecommendDto
import com.capston.domain.response.MessageResponse
import com.capston.domain.response.recommend.RecommendResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PostRecommendLecturesUseCase @Inject constructor(
    private val recommendRepository: RecommendRepository
) {
    operator fun invoke(recommendDto: RecommendDto): Flow<List<RecommendResponse>> = flow {
        val response = recommendRepository.postRecommendLectures(recommendDto)
        emit(response)
    }
}