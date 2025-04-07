package com.capston.data.repository.remote.datasourcelmpl

import com.capston.data.repository.remote.api.LoginApi
import com.capston.domain.datasource.LoginDataSource
import com.capston.domain.request.LoginDto
import com.capston.domain.response.BaseResult
import javax.inject.Inject

class LoginDataSourceImpl @Inject constructor(
    private val loginApi: LoginApi
): LoginDataSource {
    override suspend fun postLoginInfo(loginDto: LoginDto): BaseResult {
        return loginApi.postLoginInfo(loginDto)
    }
}