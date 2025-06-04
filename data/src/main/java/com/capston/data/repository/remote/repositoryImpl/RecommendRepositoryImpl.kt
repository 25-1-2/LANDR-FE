package com.capston.data.repository.remote.repositoryImpl

import com.capston.domain.datasource.RecommendDataSource
import com.capston.domain.repository.RecommendRepository
import com.capston.domain.request.RecommendDto
import com.capston.domain.response.recommend.RecommendResponse
import javax.inject.Inject

class RecommendRepositoryImpl @Inject constructor(
    private val recommendDataSource: RecommendDataSource
) : RecommendRepository {
    override suspend fun postRecommendLectures(recommendDto: RecommendDto): RecommendResponse {
        return recommendDataSource.postRecommendLectures(recommendDto)
    }
}