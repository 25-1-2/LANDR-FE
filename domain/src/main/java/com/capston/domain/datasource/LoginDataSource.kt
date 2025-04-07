package com.capston.domain.datasource

import com.capston.domain.request.LoginDto
import com.capston.domain.response.BaseResult

interface LoginDataSource {
    // 회원가입 후 사용자 정보 전송
    suspend fun postLoginInfo(loginDto: LoginDto): BaseResult
}