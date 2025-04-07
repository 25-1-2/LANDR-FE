package com.capston.data.repository.remote.repositoryImpl

import com.capston.domain.datasource.LoginDataSource
import com.capston.domain.repository.LoginRepository
import com.capston.domain.request.LoginDto
import com.capston.domain.response.BaseResult
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val loginDataSource: LoginDataSource
): LoginRepository {
    override suspend fun postLoginInfo(loginDto: LoginDto): BaseResult = loginDataSource.postLoginInfo(loginDto)
}