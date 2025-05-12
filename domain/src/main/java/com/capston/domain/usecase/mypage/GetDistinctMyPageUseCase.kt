package com.capston.domain.usecase.mypage

import com.capston.domain.repository.MyPageRepository
import com.capston.domain.response.mypage.GetDistinctMyPageResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetDistinctMyPageUseCase @Inject constructor(
    private val repository: MyPageRepository
) {
    operator fun invoke(): Flow<GetDistinctMyPageResponse> = flow {
        val response = repository.getDistinctMyPage()
        emit(response)
    }
}