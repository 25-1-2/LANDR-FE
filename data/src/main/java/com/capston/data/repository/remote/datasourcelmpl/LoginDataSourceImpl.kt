package com.capston.data.repository.remote.datasourcelmpl

import com.capston.data.repository.remote.api.LoginApi
import com.capston.domain.datasource.LoginDataSource
import com.capston.domain.request.LoginDto
import com.capston.domain.response.Result
import javax.inject.Inject

class LoginDataSourceImpl @Inject constructor(
    private val loginApi: LoginApi
): LoginDataSource {
    override suspend fun postLoginInfo(loginDto: LoginDto): Result {
        return loginApi.postLoginInfo(loginDto)
    }
}