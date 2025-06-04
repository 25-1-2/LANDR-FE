package com.capston.domain.repository

import com.capston.domain.response.recommend.RecommendResponse
import kotlinx.coroutines.flow.Flow

interface RecommendationRepository {
    suspend fun saveRecommendations(recommendations: List<RecommendResponse>)
    fun getRecommendations(): Flow<List<RecommendResponse>>
    suspend fun clearRecommendations()
}