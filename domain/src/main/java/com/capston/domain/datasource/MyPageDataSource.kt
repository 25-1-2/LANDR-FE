package com.capston.domain.datasource

import com.capston.domain.response.mypage.GetDistinctMyPageResponse

interface MyPageDataSource {
    // 마이페이지 기본 정보 조회
    suspend fun getDistinctMyPage(): GetDistinctMyPageResponse
}