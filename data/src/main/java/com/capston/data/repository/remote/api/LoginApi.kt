package com.capston.data.repository.remote.api

import com.capston.domain.request.LoginDto
import com.capston.domain.response.BaseResult
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {
    // 강의 별명 수정
    @POST("/api/users/login")
    suspend fun postLoginInfo(
        @Body loginDto: LoginDto
    ): BaseResult
}