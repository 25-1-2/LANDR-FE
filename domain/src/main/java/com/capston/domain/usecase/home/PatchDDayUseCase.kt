package com.capston.domain.usecase.home

import com.capston.domain.repository.HomeRepository
import com.capston.domain.request.UpdateDDayRequest
import com.capston.domain.response.home.DDayResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PatchDDayUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke(dDayId: Int, updateDDayRequest: UpdateDDayRequest): Flow<DDayResponse> {
        return homeRepository.patchDDay(dDayId, updateDDayRequest)
    }
}