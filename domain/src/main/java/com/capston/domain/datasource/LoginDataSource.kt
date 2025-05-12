package com.capston.domain.datasource

import com.capston.domain.request.LoginDto
import com.capston.domain.request.UserNameDto
import com.capston.domain.response.user.LoginResponse
import com.capston.domain.response.user.UserProfileResponse

interface LoginDataSource {
    // 회원가입 후 사용자 정보 전송
    suspend fun postLoginInfo(loginDto: LoginDto): LoginResponse

    // 유저 정보 조회
    suspend fun getUserProfile(): UserProfileResponse

    // 유저 이름 수정
    suspend fun patchUserName(userNameDto: UserNameDto): UserProfileResponse
}