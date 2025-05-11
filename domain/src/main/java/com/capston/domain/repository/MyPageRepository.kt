package com.capston.domain.repository

import com.capston.domain.response.mypage.GetDistinctMyPageResponse
import com.capston.domain.response.mypage.GetMyPageStatisticsResponse

interface MyPageRepository {
    // 마이페이지 기본 정보 조회
    suspend fun getDistinctMyPage(): GetDistinctMyPageResponse

    // 월별 공부 기록 통계 조회
    suspend fun getMonthlyStatistics(date: String): GetMyPageStatisticsResponse
}