package com.capston.data.repository.remote.datasourcelmpl

import com.capston.data.repository.remote.api.RecommendApi
import com.capston.domain.datasource.RecommendDataSource
import com.capston.domain.request.RecommendDto
import com.capston.domain.response.recommend.RecommendResponse
import javax.inject.Inject

class RecommendDataSourceImpl @Inject constructor(
    private val recommendApi: RecommendApi
): RecommendDataSource {
    override suspend fun postRecommendLectures(recommendDto: RecommendDto): List<RecommendResponse> {
        return recommendApi.postRecommendLectures(recommendDto)
    }
}