package com.capston.domain.usecase.login

import com.capston.domain.repository.LoginRepository
import com.capston.domain.request.UserNameDto
import com.capston.domain.response.user.UserProfileResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PatchUserNameUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    operator fun invoke(userNameDto: UserNameDto): Flow<UserProfileResponse> = flow {
        val response = repository.patchUserName(userNameDto)
        emit(response)
    }
}