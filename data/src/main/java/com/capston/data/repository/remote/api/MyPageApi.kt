package com.capston.data.repository.remote.api

import com.capston.domain.response.mypage.GetMyPageStatisticsResponse
import com.capston.domain.response.mypage.GetDistinctMyPageResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MyPageApi {
    // 마이페이지 기본 조회
    @GET("/v1/mypage")
    suspend fun getDistinctMyPage(
    ): GetDistinctMyPageResponse

    // 월별 공부 기록 통계 조회
    @GET("/v1/mypage/statistics")
    suspend fun getMonthlyStatistics(
        @Query("date") date: String
    ): GetMyPageStatisticsResponse
}