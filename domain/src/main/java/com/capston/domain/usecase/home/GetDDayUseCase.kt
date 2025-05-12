package com.capston.domain.usecase.home

import com.capston.domain.repository.HomeRepository
import com.capston.domain.response.home.DDayResponse
import com.capston.domain.response.home.DistinctHomeIdResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDDayUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke(dDayId: Int): Flow<DDayResponse> {
        return homeRepository.getDDay(dDayId)
    }
}