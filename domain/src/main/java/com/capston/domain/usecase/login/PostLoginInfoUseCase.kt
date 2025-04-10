package com.capston.domain.usecase.login

import com.capston.domain.repository.LoginRepository
import com.capston.domain.request.LoginDto
import com.capston.domain.response.BaseResult
import com.capston.domain.response.LoginResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PostLoginInfoUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    operator fun invoke(loginDto: LoginDto): Flow<LoginResponse> = flow {
        val response = repository.postLoginInfo(loginDto)
        emit(response)  // JSON 변환 없이 문자열 그대로 emit
    }
}