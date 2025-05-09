package com.capston.domain.usecase.login

import com.capston.domain.repository.LoginRepository
import com.capston.domain.request.LoginDto
import com.capston.domain.response.user.LoginResponse
import com.capston.domain.response.user.UserProfileResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    operator fun invoke(): Flow<UserProfileResponse> = flow {
        val response = repository.getUserProfile()
        emit(response)
    }
}