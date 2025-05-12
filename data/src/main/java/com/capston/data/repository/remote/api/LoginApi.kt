package com.capston.data.repository.remote.api

import com.capston.domain.request.LoginDto
import com.capston.domain.request.UserNameDto
import com.capston.domain.response.user.LoginResponse
import com.capston.domain.response.user.UserProfileResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface LoginApi {
    // 로그인
    @POST("/api/users/login")
    suspend fun postLoginInfo(
        @Body loginDto: LoginDto
    ): LoginResponse

    // 현재 사용자 프로필 조회
    @GET("/api/users/me")
    suspend fun getUserProfile(): UserProfileResponse

    // 사용자 이름 수정
    @PATCH("/api/users/me/name")
    suspend fun patchUserName(
        @Body userNameDto: UserNameDto
    ): UserProfileResponse
}