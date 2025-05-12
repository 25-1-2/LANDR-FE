package com.capston.domain.usecase.mypage

import com.capston.domain.repository.MyPageRepository
import com.capston.domain.response.mypage.GetMyPageStatisticsResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetMonthlyStatisticsUseCase @Inject constructor(
    private val repository: MyPageRepository
) {
    operator fun invoke(date: String): Flow<GetMyPageStatisticsResponse> = flow {
        val response = repository.getMonthlyStatistics(date)
        emit(response)
    }
}