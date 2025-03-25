package com.capston.domain.usecase.home

import com.capston.domain.repository.HomeRepository
import com.capston.domain.response.BaseResponse
import com.capston.domain.response.DistinctHomeIdResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDistinctHomeUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke(): Flow<DistinctHomeIdResponse> {
        return homeRepository.getDistinctHome()
    }
}