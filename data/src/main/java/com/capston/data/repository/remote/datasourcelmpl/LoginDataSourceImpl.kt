package com.capston.data.repository.remote.datasourcelmpl

import android.util.Log
import com.capston.data.repository.remote.api.LoginApi
import com.capston.domain.datasource.LoginDataSource
import com.capston.domain.request.LoginDto
import com.capston.domain.request.UserNameDto
import com.capston.domain.response.user.LoginResponse
import com.capston.domain.response.user.UserProfileResponse
import javax.inject.Inject

class LoginDataSourceImpl @Inject constructor(
    private val loginApi: LoginApi
): LoginDataSource {
    override suspend fun postLoginInfo(loginDto: LoginDto): LoginResponse {
        val response = loginApi.postLoginInfo(loginDto)
        // 액세스 토큰 로그 출력
        Log.d("LoginDataSourceImpl", "Token: ${response.token}")
        return response
    }

    override suspend fun getUserProfile(): UserProfileResponse {
        val response = loginApi.getUserProfile()
        Log.d("LoginDataSourceImpl", response.toString())
        return response
    }

    override suspend fun patchUserName(userNameDto: UserNameDto): UserProfileResponse {
        val response = loginApi.patchUserName(userNameDto)
        Log.d("LoginDataSourceImpl", response.toString())
        return response
    }
}