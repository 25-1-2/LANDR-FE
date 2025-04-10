package com.capston.domain.repository

import com.capston.domain.request.LoginDto
import com.capston.domain.response.BaseResult
import com.capston.domain.response.LoginResponse

interface LoginRepository {
    // 회원가입 후 사용자 정보 전송
    suspend fun postLoginInfo(loginDto: LoginDto): LoginResponse
}