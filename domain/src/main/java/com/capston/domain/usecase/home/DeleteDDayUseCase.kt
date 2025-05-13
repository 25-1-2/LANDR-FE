package com.capston.domain.usecase.home

import com.capston.domain.repository.HomeRepository
import javax.inject.Inject

class DeleteDDayUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke(dDayId: Int) {
        return homeRepository.deleteDDay(dDayId)
    }
}