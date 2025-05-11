package com.capston.domain.repository

import com.capston.domain.response.mypage.GetDistinctMyPageResponse


interface MyPageRepository {
    // 마이페이지 기본 정보 조회
    suspend fun getDistinctMyPage(): GetDistinctMyPageResponse
}