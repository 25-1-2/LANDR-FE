package com.capston.data.repository.remote.api

import com.capston.domain.request.RecommendDto
import com.capston.domain.response.recommend.RecommendResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface RecommendApi {
    // 로그인
    @POST("/v1/lectures/recommend")
    suspend fun postRecommendLectures(
        @Body recommendDto: RecommendDto
    ): List<RecommendResponse>
}