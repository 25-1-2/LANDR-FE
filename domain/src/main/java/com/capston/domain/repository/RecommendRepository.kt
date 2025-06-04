package com.capston.domain.repository

import com.capston.domain.request.RecommendDto
import com.capston.domain.response.recommend.RecommendResponse

interface RecommendRepository {
    suspend fun postRecommendLectures(recommendDto: RecommendDto): RecommendResponse
}
