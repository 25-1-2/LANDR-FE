package com.capston.data.repository.remote.repositoryImpl

import com.capston.data.local.storage.RecommendationStorage
import com.capston.domain.repository.RecommendationRepository
import com.capston.domain.response.recommend.RecommendResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecommendationRepositoryImpl @Inject constructor(
    private val recommendationStorage: RecommendationStorage
) : RecommendationRepository {

    override suspend fun saveRecommendations(recommendations: List<RecommendResponse>) {
        recommendationStorage.saveRecommendations(recommendations)
    }

    override fun getRecommendations(): Flow<List<RecommendResponse>> {
        return recommendationStorage.getRecommendations()
    }

    override suspend fun clearRecommendations() {
        recommendationStorage.clearRecommendations()
    }
}