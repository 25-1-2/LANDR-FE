package com.capston.domain.datasource

import com.capston.domain.request.RecommendDto
import com.capston.domain.response.recommend.RecommendResponse

interface RecommendDataSource {
    suspend fun postRecommendLectures(
        recommendDto: RecommendDto
    ): List<RecommendResponse>
}